```
JAVA_OPTS="-Dlog.service.output=/dev/stdout -Dlog.access.output=/dev/stdout" ./sbt run
```

# Glossary
* Experiment - a particular test you want to run. There will be different variants (Arms) that get selected. Testing if a user prefers clicking on `Red`, `Green`, or `Blue` links is an experiment.
* Arm - A version of the thing that you are trying to test. The three different Arms in our experiment are `Arm("red")`, `Arm("green")`, and `Arm("blue")`.
* Pull - The act of deciding which Arm will be used for a particular request/impression/session/whatever. Which Arm is used will be decided by the bandit algorithm.
* Reward - This is the _outcome_ of a particular interaction with a user. The reward could be 1.0 or 0.0 if a user does/does not click. It could be the total dollars spent after clicking, and so on.
* Request Count - Each Arm has a _request count_. This is how many times this Arm was returned in a HTTP request.
* Pull Count - This is how many times a _reward has been registered_ for the particular arm. If you report a reward value back for every single request, this should be approximately equal to the Request Count.

# Lifecycle

Process for creating an experiment, making some requests, and reporting reward:

First create a new experiment. We will use an `EpsilonGreedy` bandit with an epsilon value of 0.5, and three Arms:

```bash
curl -d '{"name":"adam", "epsilon": 0.5, "arms": ["moe", "larry", "curly"]}' -H "Content-Type: application/json" -X POST http://localhost:8888/create/epsilon_greedy | jq .
```

The result: a new experiment called `adam` with three arms: `moe`, `larry`, and `curly`.
```json
{
  "status": "success",
  "data": {
    "name": "adam",
    "top_arm": {
      "id": "curly",
      "pull_count": 0,
      "request_count": 0,
      "value": 0
    },
    "arms": {
      "curly": {
        "id": "curly",
        "pull_count": 0,
        "request_count": 0,
        "value": 0
      },
      "larry": {
        "id": "larry",
        "pull_count": 0,
        "request_count": 0,
        "value": 0
      },
      "moe": {
        "id": "moe",
        "pull_count": 0,
        "request_count": 0,
        "value": 0
      }
    },
    "id": 1,
    "isLive": false
  }
}
 ```

Note that the experiment is _not yet live_. We need to enable that.

```bash
curl -d '{"id":1, "is_live": true}' -H "Content-Type: application/json" -X POST http://localhost:8888/activate | jq .
```
```json
{
  ...,
  "id": 1,
  "isLive": true
}
```

Now that it's live, we can ask it for which Arm to display first:
```bash
curl -d '{"experiment_id":1}' -H "Content-Type: application/json" -X POST http://localhost:8888/pull | jq .
```
```json
{
  "id": "curly",
  "value": 0
}
```
It decided we wanted to show `curly` first. Let's say the user liked it, so we report a value of `10.0`
```bash
curl -d '{"experiment_id":1, "arm_id": "curly", "value": 10.0}' -H "Content-Type: application/json" -X POST http://localhost:8888/reward | jq .
```

```json
{
  "name": "adam",
  "top_arm": {
    "id": "curly",
    "pull_count": 1,
    "request_count": 1,
    "value": 10
  },
  "arms": {
    "curly": {
      "id": "curly",
      "pull_count": 1,
      "request_count": 1,
      "value": 10
    },
    "larry": {
      "id": "larry",
      "pull_count": 0,
      "request_count": 0,
      "value": 0
    },
    "moe": {
      "id": "moe",
      "pull_count": 0,
      "request_count": 0,
      "value": 0
    }
  },
  "id": 1,
  "isLive": true
}
```

Now we've updated the value associated with the `curly` arm, and that information will be used the next time we `pull`. 


# Up next
- Use a real database instead of just a `collection.mutable.Map`
- Initialize experiments with pre-set weights
- Incorporate the other bandit algorithms available in [sifter-lib](http://www.github.com/alaiacano/sifter-lib)
- "Tests"
