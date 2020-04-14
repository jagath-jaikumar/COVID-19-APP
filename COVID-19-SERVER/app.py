from flask import Flask,request
from tinydb import TinyDB, Query, where
from scores import Score
import json

app = Flask(__name__)

db = TinyDB("main.db")
locations = db.table('locations')

@app.route("/store-location", methods=["GET","POST"])
def store_loc():
    if request.method == 'POST':
        try:
            locations.insert(request.json)
        except:
            return {"result":"store failed"}
        return {"result":"Stored successfully"}
    else:
        return {"result":"not implemented yet"}

@app.route("/get-all-locations/<mac>", methods=["GET"])
def get_loc(mac):
    locs = locations.search(where('User') == mac)
    score = calculateScore(locs)
    return {"result":"Success","locations":locs, "score":score}


@app.route("/get-last-locations/<mac>", methods=["GET"])
def get_loc_last(mac):
    locs = locations.search(where('User') == mac)
    return {"result":"Success","location":sorted(locs, key = lambda i: int(i['Timestamp']),reverse=True)[0]}


@app.route("/get-last-locations-day/<mac>/<timestamp>", methods=["GET"])
def get_loc_last_day(mac,timestamp):
    locs = locations.search(where('User') == mac)

    last_day_locs = []

    cur_time = timestamp
    last_day = int(timestamp) - 86400

    for loc in locs:
        if int(loc['Timestamp']) > last_day:
            last_day_locs.append(loc)

    score = calculateScore(last_day_locs)
    return {"result":"Success","location":last_day_locs, "score":score}

def calculateScore(locations):
    s = Score(locations)
    return s.social_distance_score()


@app.route("/")
def hello():
    return "My COVID-19 App Server"



@app.route("/admin-cleardb",methods=["GET"])
def clear_db():
    db.purge_table('locations')
    return "cleared"



if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0',ssl_context='adhoc')


# powered by bee
