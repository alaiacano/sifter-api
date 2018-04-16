```
JAVA_OPTS="-Dlog.service.output=/dev/stdout -Dlog.access.output=/dev/stdout" ./sbt run
```


Process for creating an experiment, making some requests, and reporting reward:

```bash
curl -d '{"name":"adam", "algo":"epsilon_greedy", "epsilon": 0.5, "arms": ["moe", "larry", "curly"]}' -H "Content-Type: application/json" -X POST http://localhost:8888/create
curl -d '{"id":1, "is_live": true}' -H "Content-Type: application/json" -X POST http://localhost:8888/activate

curl -d '{"experiment_id":1}' -H "Content-Type: application/json" -X POST http://localhost:8888/pull

curl -d '{"experiment_id":1, "arm_id": "moe", "value": 3.1}' -H "Content-Type: application/json" -X POST http://localhost:8888/reward
curl -d '{"experiment_id":1, "arm_id": "larry", "value": 3.6}' -H "Content-Type: application/json" -X POST http://localhost:8888/reward
curl -d '{"experiment_id":1, "arm_id": "curly", "value": -1.0}' -H "Content-Type: application/json" -X POST http://localhost:8888/reward

```