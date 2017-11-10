# CSSE 477 Web server


[![build status](https://ada.csse.rose-hulman.edu/gilderjw/CSSE_477_Web_Server/badges/master/build.svg)](https://ada.csse.rose-hulman.edu/gilderjw/CSSE_477_Web_Server/commits/master)

[![coverage report](https://ada.csse.rose-hulman.edu/gilderjw/CSSE_477_Web_Server/badges/master/coverage.svg)](https://ada.csse.rose-hulman.edu/gilderjw/CSSE_477_Web_Server/commits/master)

[![uml diagram](./res/design.png)](./res/design.png)
[![module diagram](./res/module.png)](./res/module.png)

## Availability

Source: User

Stimulus: Web request

Environment: Heavy illegitimate load

Artifact: Process

Response: Resource requested

Response Measure: no downtime (latency)

Test plan:

1: Start web server

2: Run locust on server

3: Time latency for request on client

Results:

Without DoS Protection
[3.750102996826172, 1.2471940517425537, 0.9006390571594238, 9.755207061767578, 0.9632279872894287, 0.7301759719848633, 0.7221381664276123, 0.7103118896484375, 0.724419116973877, 0.7216911315917969, 0.7108488082885742, 0.7181990146636963, 0.7238378524780273, 0.706387996673584, 0.7138299942016602, 0.7138988971710205, 0.711651086807251, 1.0999479293823242, 0.737403154373169, 3.71492600440979]                                                                                  
Average: 1.53880190849 seconds

With DoS Protection
[0.7097899913787842, 0.7514331340789795, 0.7171230316162109, 0.717756986618042, 0.72725510597229, 0.7136690616607666, 0.7147819995880127, 0.7069170475006104, 0.7185790538787842, 0.7323319911956787, 0.7110269069671631, 0.7134740352630615, 0.7093410491943359, 0.7196080684661865, 0.7170989513397217, 0.7286899089813232, 0.7261590957641602, 0.7258999347686768, 0.7348270416259766, 0.7448298931121826]                                                                             
Average: 0.722029614449 seconds 

## Performance

Source: User

Stimulus: Web request

Environment: Normal load

Artifact: process

Response: resource requested

Response Measure:  average latency between requests

Test plan:

1: Start server

2: Request large file

3: Measure time to receive file

Results (for GET requests):

2017-11-02 20:34:25.135 [Thread-20] INFO  server.ConnectionHandler - Nanoseconds it took to handle response WITHOUT caching: 1563933
2017-11-02 20:34:25.855 [Thread-21] INFO  server.ConnectionHandler - Nanoseconds it took to handle response WITHOUT caching: 1130719
2017-11-02 20:34:26.480 [Thread-22] INFO  server.ConnectionHandler - Nanoseconds it took to handle response WITHOUT caching: 1915037
2017-11-02 20:34:27.107 [Thread-23] INFO  server.ConnectionHandler - Nanoseconds it took to handle response WITHOUT caching: 1162365
2017-11-02 20:34:27.688 [Thread-24] INFO  server.ConnectionHandler - Nanoseconds it took to handle response WITHOUT caching: 1564788
2017-11-02 20:34:28.260 [Thread-25] INFO  server.ConnectionHandler - Nanoseconds it took to handle response WITHOUT caching: 1278260
2017-11-02 20:34:28.804 [Thread-26] INFO  server.ConnectionHandler - Nanoseconds it took to handle response WITHOUT caching: 1131574
2017-11-02 20:34:29.406 [Thread-27] INFO  server.ConnectionHandler - Nanoseconds it took to handle response WITHOUT caching: 1270134
2017-11-02 20:34:29.986 [Thread-28] INFO  server.ConnectionHandler - Nanoseconds it took to handle response WITHOUT caching: 1416819
2017-11-02 20:34:30.588 [Thread-29] INFO  server.ConnectionHandler - Nanoseconds it took to handle response WITHOUT caching: 1658444
2017-11-02 20:44:49.590 [Thread-5] INFO  server.ConnectionHandler - Nanoseconds it took to handle response WITH caching: 1214111
2017-11-02 20:44:50.607 [Thread-6] INFO  server.ConnectionHandler - Nanoseconds it took to handle response WITH caching: 1409549
2017-11-02 20:44:51.281 [Thread-7] INFO  server.ConnectionHandler - Nanoseconds it took to handle response WITH caching: 1076834
2017-11-02 20:44:51.906 [Thread-8] INFO  server.ConnectionHandler - Nanoseconds it took to handle response WITH caching: 1160654
2017-11-02 20:44:52.524 [Thread-9] INFO  server.ConnectionHandler - Nanoseconds it took to handle response WITH caching: 953242
2017-11-02 20:44:53.184 [Thread-10] INFO  server.ConnectionHandler - Nanoseconds it took to handle response WITH caching: 1126014
2017-11-02 20:44:53.810 [Thread-11] INFO  server.ConnectionHandler - Nanoseconds it took to handle response WITH caching: 1207696
2017-11-02 20:44:54.419 [Thread-12] INFO  server.ConnectionHandler - Nanoseconds it took to handle response WITH caching: 1672130
2017-11-02 20:44:55.014 [Thread-13] INFO  server.ConnectionHandler - Nanoseconds it took to handle response WITH caching: 1117889
2017-11-02 20:44:55.662 [Thread-14] INFO  server.ConnectionHandler - Nanoseconds it took to handle response WITH caching: 860868

Average without caching: 1309207.3 nanoseconds

Average with caching: 1179898.7 nanoseconds

Percent Gain: 9.88% improvement

## Security

Source: Outside attacker

Stimulus: Try to access sensitive files

Environment: normal conditions

Artifact: system

Response: no file sent

Response Measure: no file leaks

Test plan:

1: Start server

2: Request sensitive file

3: Server does not send it

## Application Information
### Team Bruh Chat and HashMap as a Service

Description: This application hits up a microservice for a hashmap giving incrementing numbers, along with another microservice for a chatbox.

```
Feature 1: Retrieve chat stuff
    Method: GET
    URI:    /chatroom/msgtest.json/
    Request Body:
        <none>
    Response Body:
        {
            "code": 200,
            "message": "OK",
            "payload":
            [
                {
                    "username": "JimBob",
                    "mesage": "JimBob is super rad."
                }
            ]
        }
        
```

```
Feature 2: Post chat stuff
    Method: POST
    URI:    /chatroom/msgtest.json/
    Request Body:
        {
            "username": "JimBob",
            "mesage": "JimBob is super rad."
        }
    Response Body:
        {
            "code": 200,
            "message": "OK",
            "payload":
            [
                {
                    "usesrname": "JimBob",
                    "mesage": "JimBob is super rad."
                }
            ]
        }
        
```


```
Feature 3: Retrieve hash stuff
    Method: GET
    URI:    /HaaS/
    Request Body:
        <none>
    Response Body:
        {
            "code": 200,
            "message": "OK",
            "payload":
            [
                {
                    "hashString": "JimBob",
                    "value": 12
                }
            ]
        }

```

```
Feature 4: Create hash stuff
    Method: POST
    URI:    /HaaS/
    Request Body:
        {
            "hashString": "JimBob",
            "value": 0
        }
    Response Body:
        {
            "code": 200,
            "message": "OK",
            "payload":
            [
                {
                    "hashString": "JimBob",
                    "value": 12
                }
            ]
        }
```

```
Feature 5: Update hash stuff
    Method: PUT
    URI:    /HaaS/
    Request Body:
        {
            "hashString": "JimBob",
            "value": 1
        }
    Response Body:
        {
            "code": 200,
            "message": "OK",
            "payload":
            [
                {
                    "hashString": "JimBob",
                    "value": 1
                }
            ]
        }
        
```

```
Feature 6: Delete hash stuff
    Method: DELETE
    URI:    /HaaS/
    Request Body:
        {
            "hashString": "JimBob",
            "value": 12
        }
    Response Body:
        {
            "code": 200,
            "message": "OK",
            "payload": []
        }
```