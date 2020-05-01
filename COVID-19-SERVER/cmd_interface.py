## TODO
import os
import requests
import argparse
from secrets import Secret as s
import time

import os, ssl
if (not os.environ.get('PYTHONHTTPSVERIFY', '') and getattr(ssl, '_create_unverified_context', None)):
    ssl._create_default_https_context = ssl._create_unverified_context

HOST = "https://127.0.0.1:5000"

parser = argparse.ArgumentParser(description="Control the server hosting Covid19")
parser.add_argument('-c', action='store_true')
args = parser.parse_args()

if args.c:
    session = requests.Session()
    session.verify = False
    r = session.get(os.path.join(HOST, 'admin-cleardb'))
    print(r.text)





hook = "get-last-locations-day/" +  "2:15:b2:0:0:0" + "/" + "1588370697"
session = requests.Session()
session.verify = False
r = session.get(os.path.join(HOST, hook))
result = r.json()


print("score 1: {}, score 2: {}".format(result['score1']+1, result['score2']+1))
