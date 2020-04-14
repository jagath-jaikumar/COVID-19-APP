from tinydb import TinyDB, Query, where

db = TinyDB("main.db")
locations = db.table('locations')

class Score:
    def __init__(self, locations = None):
        self.locations = locations

    def social_distance_score(self):
        return 0
