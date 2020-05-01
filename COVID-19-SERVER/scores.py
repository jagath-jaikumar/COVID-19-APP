from tinydb import TinyDB, Query, where
import math

db = TinyDB("main.db")
locations = db.table('locations')

drive_max = 10
interaction_max = 100
latlng_threshold = 0.000001



def distance(loc1, loc2):
    x1 = float(loc1['Latitude'])
    x2 = float(loc2['Latitude'])

    y1 = float(loc1['Longitude'])
    y2 = float(loc2['Longitude'])

    distance = math.sqrt( ((x1-x2)**2)+((y1-y2)**2) )
    return distance


def isClose(loc1, loc2):
    dist = distance(loc1, loc2)
    if dist < latlng_threshold:
        return True
    return False

def countNear(loc):
    me = loc['User']
    time = loc['Timestamp']
    all = locations.all()
    count = 0
    for l in all:
        if not l['User'] == me:
            if l['Timestamp'] == time:
                if isClose(l,loc):
                    count+=1
    return count

class Score:


    def __init__(self, locations = None, timestamp = None):
        self.locations = locations
        self.timestamp = timestamp

    def social_distance_score(self):

        tot = 0
        for i in range(len(self.locations)-1):
            dist = distance(self.locations[i], self.locations[i+1])
            tot += dist

        score1 = int(drive_max - tot - 1)
        if score1 < 0:
            score1 = 0
        if score1 > 9:
            score1 = 9


        tot2 = 0
        for loc in self.locations:
            num_near = countNear(loc)
            tot2+= num_near


        score2 = 10 - int(tot2/interaction_max) - 1
        if score2 < 0:
            score2 = 0
        if score2 > 9:
            score2 = 9

        return score1, score2
