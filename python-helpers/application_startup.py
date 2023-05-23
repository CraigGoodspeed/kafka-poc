import json
import os

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
    http.request("POST",
                 "http://localhost:8088",
                 headers={
                     "Accept": "application/vnd.ksql.v1+json",
                     "Content-Type" : "application/json"
                 },
                 body=post_me
                 )

def post_ksql_files(folder):
    for file in os.listdir(folder):
        if file.endswith(".ksql"):
            do_post(os.path.join(folder, file))

if __name__ == '__main__':
    for str in ("../app/ksql","../dependent-data/ksql"):
        post_ksql_files(str)
    print()