**Sifter** is an API for running [multi-armed bandit](https://en.wikipedia.org/wiki/Multi-armed_bandit) experiments.

It's based on:

* [finatra](https://github.com/twitter/finatra) for the web server
* [sifter-lib](https://github.com/alaiacano/sifter-lib) for the bandit algorithms

To run:

```
JAVA_OPTS="-Dlog.service.output=/dev/stdout -Dlog.access.output=/dev/stdout" ./sbt run
```

# Status
You are looking at the first commit, based on pretty much all of the default Twitter Server options.

# Glossary
* Experiment - a particular test you want to run. There will be different variants (Arms) that get selected. Testing if a user prefers clicking on `Red`, `Green`, or `Blue` links is an experiment.
* Arm - A version of the thing that you are trying to test. The three different Arms in our experiment are `Arm("red")`, `Arm("green")`, and `Arm("blue")`.
* Pull - The act of deciding which Arm will be used for a particular request/impression/session/whatever. Which Arm is used will be decided by the bandit algorithm.
* Reward - This is the _outcome_ of a particular interaction with a user. The reward could be 1.0 or 0.0 if a user does/does not click. It could be the total dollars spent after clicking, and so on.
* Request Count - Each Arm has a _request count_. This is how many times this Arm was returned in a HTTP request.
* Pull Count - This is how many times a _reward has been registered_ for the particular arm. If you report a reward value back for every single request, this should be approximately equal to the Request Count.

# Experiment Lifecycle

Let's walk through an example of creating an experiment, making some requests, and reporting reward.

The goal of the experiment: Show people Three Stooges video that is Moe-centric, Larry-centric, or Curly-centric. Whichever stooge the people like most, we will show the most.

First create a new experiment. We will use an `EpsilonGreedy` bandit with an epsilon value of 0.5 (_extremely_ exploratory), and an Arm for each Stooge:

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

Which sets `isLive` to `true`:

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

The Epsilon Greedy algorithm decided we wanted to show `curly`. So we show that video. Let's say the viewer watched it for 10 seconds, so we report a value of `10.0`

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

The next time we want to show a video, we will `pull` again, be assigned a Stooge to show, and then report back how many seconds the viewer watched the video. Eventually, the most popular Stooge will be played the most.

# Up next for this project
- Use a real datastore instead of just a `collection.mutable.Map`
- Initialize experiments with pre-set weights
- "Tests"
