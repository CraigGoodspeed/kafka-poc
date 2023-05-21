import json
import urllib.request
import urllib3

http = urllib3.PoolManager()
endpoint = "http://localhost:8081/subjects"

def perform_get(url):
    return http.request('GET', url).data

def get_all_subjects():
    return json.loads(perform_get(endpoint))

def remove_items():
    for subject in get_all_subjects():
        http.request('DELETE', f"{endpoint}/{subject}")


if __name__ == '__main__':
    remove_items()