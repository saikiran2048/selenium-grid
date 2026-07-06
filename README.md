# Selenium Grid + Jenkins Matrix + K8s Pipeline (Practice Project)

## Prereqs
- Docker + Docker Compose
- Java 17, Maven
- (Optional) Jenkins with the "Matrix" and "Pipeline" plugins, and `kubectl` + a local cluster (kind/minikube) for the K8s stretch goal.

## 1. Run the Grid locally
```
docker compose up -d --scale chrome-node=2 --scale firefox-node=2
```
Check it came up: open http://localhost:4444/ui (Grid console) — you should see 2 Chrome + 2 Firefox nodes registered.

## 2. Run the tests against the Grid
```
mvn clean test -Dbrowser=chrome -Denvironment=qa -DgridUrl=http://localhost:4444/wd/hub
mvn clean test -Dbrowser=firefox -Denvironment=qa -DgridUrl=http://localhost:4444/wd/hub
```
Watch the Grid console while it runs — you'll see sessions appear on the nodes in real time.

## 3. Tear down
```
docker compose down -v
```

## 4. Jenkins matrix run
Point a Jenkins Pipeline job at this repo (Jenkinsfile is at the root). It needs a Jenkins agent with Docker + Maven available. The matrix stage will spin up 4 parallel cells: chrome-qa, chrome-staging, firefox-qa, firefox-staging.

## 5. Kubernetes stretch goal (local)
```
kubectl apply -f k8s/selenium-grid.yaml
kubectl port-forward svc/selenium-hub 4444:4444
```
Then run the same `mvn test` commands from step 2 unchanged — the framework doesn't know or care whether the Hub is a Docker Compose container or a K8s pod, it's just a URL.

## 6. Run it for free in the cloud (GitHub Actions)

This repo includes `.github/workflows/ci.yml`, which runs the **exact same
docker-compose.yml** unchanged, on GitHub's free hosted runners:

1. Push this project to a new **public** GitHub repo (private repos get free
   minutes too, just a monthly cap).
2. Go to the **Actions** tab — the workflow runs automatically on push.
3. You'll see 4 parallel jobs: `chrome/qa`, `chrome/staging`, `firefox/qa`,
   `firefox/staging` — GitHub's native `strategy: matrix`, the direct
   equivalent of the Jenkins `matrix {}` block, just running on GitHub's
   infra instead of a Jenkins agent.
4. A 5th job spins up a real (if small) Kubernetes cluster with `kind`
   ("Kubernetes IN Docker"), applies `k8s/selenium-grid.yaml` unchanged, and
   runs a test against it — a genuine `kubectl apply`, not a simulation.
5. Each job uploads its own Surefire report as a downloadable artifact.

This costs nothing, needs no server you maintain, and gives you a shareable
link with real run history — good enough to pull up live in an interview and
say "here, this ran an hour ago."

## 7. If you specifically want Jenkins running somewhere (not just the Jenkinsfile)

Options, cheapest/most-realistic first:

- **Oracle Cloud Free Tier** — the only "free forever" tier generous enough
  (4 ARM CPUs / 24GB RAM) to run Jenkins + Grid + 2 browser nodes
  comfortably, indefinitely, for $0. AWS/GCP free-tier VMs (1GB RAM) are too
  small for this and will OOM.
- **GitHub Codespaces** (60 free hrs/month) or **labs.play-with-docker.com**
  (free, 4-hour sandbox sessions, no signup) — spin up a throwaway Jenkins
  container right before an interview to do a live click-through of a real
  Jenkins matrix build, then let it expire.
- Locally via Docker (`docker run jenkins/jenkins`) is still the easiest way
  to rehearse this before Saturday.

