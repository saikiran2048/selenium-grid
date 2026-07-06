pipeline {
    agent any

    // Grid must be up before any matrix cell runs. We bring it up once,
    // in a "Start Grid" stage, and tear it down once at the very end -
    // NOT per matrix cell, otherwise cells would race to start/stop the
    // same containers and you'd get port-already-allocated failures.
    options {
        timestamps()
        disableConcurrentBuilds() // avoid two Grid stacks fighting over port 4444
    }

    stages {

        stage('Start Selenium Grid') {
            steps {
                sh 'docker compose -f docker-compose.yml up -d --scale chrome-node=2 --scale firefox-node=2'
                // Don't just sleep - poll the Hub's own readiness endpoint.
                // The Hub returns {"value":{"ready":true,...}} once enough
                // nodes have registered. Sleeping a fixed N seconds is the
                // #1 cause of "works on my machine, flaky in CI" Grid pipelines.
                // Caveat vs. the GitHub Actions version of this same check: GitHub's
                // ubuntu-latest runners ship jq preinstalled, but a Jenkins agent
                // might not. If this fails with "jq: command not found", add a
                // one-time `sh 'which jq || apt-get install -y jq'` to your agent
                // provisioning, or install it in a custom Jenkins agent image.
                sh '''
                    for i in $(seq 1 40); do
                        status=$(curl -s http://localhost:4444/status || true)
                        ready=$(echo "$status" | jq -r '.value.ready' 2>/dev/null || echo "false")
                        if [ "$ready" = "true" ]; then
                            echo "Grid is ready"
                            exit 0
                        fi
                        echo "Waiting for Grid... ($i/40): $status"
                        sleep 5
                    done
                    echo "Grid did not become ready in time - dumping container logs:"
                    docker compose logs
                    exit 1
                '''
            }
        }

        stage('Cross-browser / Cross-environment Tests') {
            matrix {
                // Every combination of axes becomes its own parallel Jenkins
                // stage: chrome-qa, chrome-staging, firefox-qa, firefox-staging.
                // Jenkins schedules all 4 as concurrently as agent/executor
                // capacity allows - this is the CI-side parallelism, separate
                // from (and layered on top of) TestNG's in-JVM parallel="methods".
                axes {
                    axis {
                        name 'BROWSER'
                        values 'chrome', 'firefox'
                    }
                    axis {
                        name 'ENVIRONMENT'
                        values 'qa', 'staging'
                    }
                }

                stages {
                    stage('Run Suite') {
                        steps {
                            echo "Running ${BROWSER} against ${ENVIRONMENT}"
                            sh """
                                mvn clean test \
                                    -Dbrowser=${BROWSER} \
                                    -Denvironment=${ENVIRONMENT} \
                                    -DgridUrl=http://localhost:4444/wd/hub
                            """
                        }
                        post {
                            always {
                                // Unique report name per cell so 4 concurrent
                                // stages don't overwrite each other's surefire output.
                                sh "mkdir -p reports/${BROWSER}-${ENVIRONMENT}"
                                sh "cp -r target/surefire-reports/* reports/${BROWSER}-${ENVIRONMENT}/ || true"
                                junit allowEmptyResults: true, testResults: "target/surefire-reports/*.xml"
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            // Always tear the Grid down, pass or fail, so containers/ports
            // don't leak between builds.
            sh 'docker compose -f docker-compose.yml down -v'
            archiveArtifacts artifacts: 'reports/**', allowEmptyArchive: true
        }
        failure {
            echo 'Pipeline failed - check the matrix cell logs above for which browser/environment combo broke.'
        }
    }
}