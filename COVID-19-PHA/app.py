import requests
import os
from secrets import Secret as s
from urllib3.exceptions import InsecureRequestWarning
import time
import numpy as np
from flask import Flask
import dash
import dash_daq as daq
from dash.dependencies import Input, Output, State
import dash_core_components as dcc
import dash_bootstrap_components as dbc
import dash_html_components as html
import pandas as pd

import gmaps

requests.packages.urllib3.disable_warnings(category=InsecureRequestWarning)
ip = s.ip
api = s.api


data = pd.DataFrame(None, columns = ["Latitude", "Longitude", "Magnitude"])






def refresh():
    popularplaces = "popular-places-day/" + str(int(time.time())) + "/web"
    r = requests.get(os.path.join(ip, popularplaces), verify=False)
    places = r.json()["result"]

    for i in range(len(places)):
        data.loc[i] = [places[i]['Lat'], places[i]['Lon'], 1]
    print(data)

refresh()
app = dash.Dash(__name__,
                external_stylesheets=[dbc.themes.PULSE],
                assets_folder='assets')


divs = []

app.layout = html.Div([
    dcc.Location(id='url', refresh=False),
    html.Div(id='page-content')
])


index_page = html.Div([
    html.Div([
        html.Div([
            html.H2('Public Health Heatmap', className='display-4'),
            html.P('Here is where a public health official would get an overview of where people are', className='lead'),
            html.Hr(className="my-4"),
            html.P('This heatmap is updated in real time'),
            html.A('Refresh',className='btn btn-primary btn-lg', href="/",role="button")
        ],
        className='container text-center'
        )
        ],className='jumbotron')


    ,
    html.Div(divs, className='container')
])


def make_map():
    import plotly.express as px
    fig = px.density_mapbox(data, lat='Latitude', lon='Longitude', z='Magnitude', radius=10,
                        center=dict(lat=0, lon=180), zoom=0,
                        mapbox_style="stamen-terrain")

    divs.append(html.Div([dcc.Graph(figure=fig)]))

make_map()


# Update the index
@app.callback(dash.dependencies.Output('page-content', 'children'),
              [dash.dependencies.Input('url', 'pathname')])
def display_page(pathname):
    if pathname == '/' or pathname == '':
        refresh()
        return index_page




if __name__ == '__main__':
    app.server.run(debug=True, threaded=True, host='0.0.0.0')
