import json

import urllib3

http = urllib3.PoolManager()

if __name__ == '__main__':
    data_to_send = json.dumps({"ksql":(open("../ksql-for-poc/customer_product_price_insert.ksql","r").read())})
    response = http.request(
        url="http://localhost:8088/ksql",
        method="POST",
        headers={"Accept": "application/vnd.ksql.v1+json","Content-Type":"application/json"},
        body=data_to_send)
    print(response.data)