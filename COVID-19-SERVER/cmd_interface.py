## TODO
import os
import requests
import argparse
from secrets import Secret as s

HOST = "https://127.0.0.1:5000"

parser = argparse.ArgumentParser(description="Control the server hosting Covid19")
parser.add_argument('-c', action='store_true')
args = parser.parse_args()

if args.c:
    session = requests.Session()
    session.verify = False
    r = session.get(os.path.join(HOST, 'admin-cleardb'))
    print(r.text)


if not args.c:
    print("No action selected ...")
