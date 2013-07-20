from widgets import BrowseFileText
from panels import BaseWindow
from PyQt4 import QtGui as gui, QtCore as core
from controllers.asciiconvertercontroller import *


class AsciiConverterWindow(BaseWindow):

    def __init__(self, parent=None):
        super(BaseWindow, self).__init__(parent=parent)
        self.initGui()

    def initGui(self):
        self.sourceFile = BrowseFileText(self, isDir=False)
        self.sourceFile.initGui(self, 'Source File Name', 10, 10)

        self.targetFile = BrowseFileText(self, isDir=False)
        self.targetFile.initGui(self, 'Target File Name', 10, 60)

        self.outputFileType = gui.QComboBox(self)
        self.outputFileType.addItem(core.QString('Hazard Map'))
        self.outputFileType.addItem(core.QString('Hazard Curve'))
        self.outputFileType.addItem(core.QString('UH Spectra'))
        self.outputFileType.setGeometry(260, 100, 150, 30)

        converter = gui.QPushButton('Convert', self)
        converter.setGeometry(410, 150, 120, 40)
        converter.clicked.connect(self.__convertFile)

    def __convertFile(self):
        s = self.sourceFile.getSelectedFile()
        t = self.targetFile.getSelectedFile()
        type = self.outputFileType.currentIndex()
        func = None

        if type == 0:
            func = convertHazardMapToAscii
        elif type == 1:
            func = convertHazardCurveToAscii
        elif type == 2:
            func = convertUhSpectraToAscii

        self.callFunction(lambda: func(s, t), 'Conversion is successful')