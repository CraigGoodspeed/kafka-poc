import time

import urllib3

http = urllib3.PoolManager()


def wait():
    print("registry is not available waiting...")
    time.sleep(1)
    ensure_ready()


def ensure_ready():
    try:
        response = http.request("GET", "http://localhost:8081/subjects")
        if response.status != 200:
            wait()
    except:
        wait()


if __name__ == '__main__':
    ensure_ready()
