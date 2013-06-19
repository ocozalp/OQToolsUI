'''
Entry point for openquake admin

Created on Mar 28, 2013

@author: orhan
'''

import shape_parser.shapefile as shp
from converters.shapefileconverter import  ShapeFileConverter
from controllers.convertercontroller import *

import sys
from main_window import MainWindow
from PyQt4 import QtGui as gui


def main():
    app = gui.QApplication(sys.argv)
    mw = MainWindow()
    mw.show()
    sys.exit(app.exec_())

def deneme():
    sf = shp.Reader('/home/orhan/Desktop/LogFolders/20130613/new_shape/EMME_AsModel_Branch01.dbf')

    for field in sf.fields:
        print field

def deneme2():
    convert('/home/orhan/Desktop/LogFolders/20130618/shape/EMME_AsModel_ver2_4_BRANCH02_adjusted.shp',
            '/home/orhan/Desktop/asd.xml')

if __name__ == '__main__':
    main()