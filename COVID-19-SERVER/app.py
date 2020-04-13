from flask import Flask,request
from tinydb import TinyDB, Query, where
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
            print()
            return {"result":"store failed"}

        return {"result":"Stored successfully"}


    else:
        return {"result":"not implemented yet"}

@app.route("/get-all-locations/<mac>", methods=["GET"])
def get_loc(mac):
    print(mac)
    locs = locations.search(where('User') == mac)
    print(locs)
    return {"result":"Success","locations":locs}






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
