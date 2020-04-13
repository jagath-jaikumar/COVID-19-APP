## TODO
import os
import requests
import argparse
from secrets import Secret as s

HOST = s.ip

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
