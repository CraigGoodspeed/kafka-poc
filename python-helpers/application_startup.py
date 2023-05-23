import json
import os
import time

import urllib3

http = urllib3.PoolManager()


class post_body:
    def __init__(self, filename):
        self.ksql = open(filename, "r").read()
        self.streamProperties = {
            "ksql.streams.auto.offset.reset": "earliest"
        }


def do_post(file):
    obj = post_body(file)
    post_me = json.dumps(obj.__dict__)
    response = http.request(method="POST",
                            url="http://localhost:8088/ksql",
                            headers={
                                "Accept": "application/vnd.ksql.v1+json",
                                "Content-Type": "application/json"
                            },
                            body=post_me
                            )
    response_data = json.loads(response.data)
    if "error_code" in response_data:
        print(response_data)
    if "commandStatus" in response_data:
        print(response_data["commandStatus"])


def post_ksql_files(folder):
    for file in sorted(os.listdir(folder)):
        if file.endswith(".ksql"):
            do_post(os.path.join(folder, file))


def wait_for_server():
    resp = http.request("GET", "http://localhost:8088/healthcheck")
    resp_data = json.loads(resp.data)
    if "isHealthy" not in resp_data:
        print("server not ready waiting")
        time.sleep(0.5)
        wait_for_server()
    else:
        print(resp_data)


if __name__ == '__main__':
    wait_for_server()
    for val in ("../app/ksql", "../dependent-data/ksql"):
        post_ksql_files(val)
    print()
