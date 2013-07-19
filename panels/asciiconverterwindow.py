from widgets import BrowseFileText
from panels import BaseWindow
from PyQt4 import QtGui as gui
from controllers.asciiconvertercontroller import convertHazardMapToAscii


class AsciiConverterWindow(BaseWindow):

    def __init__(self, parent=None):
        super(BaseWindow, self).__init__(parent=parent)
        self.initGui()

    def initGui(self):
        self.sourceFile = BrowseFileText(self, isDir=False)
        self.sourceFile.initGui(self, 'Source File Name', 10, 10)

        self.targetFile = BrowseFileText(self, isDir=False)
        self.targetFile.initGui(self, 'Target File Name', 10, 60)

        converter = gui.QPushButton('Convert', self)
        converter.setGeometry(410, 150, 120, 40)
        converter.clicked.connect(self.__convertFile)

    def __convertFile(self):
        s = self.sourceFile.getSelectedFile()
        t = self.targetFile.getSelectedFile()

        self.callFunction(lambda: convertHazardMapToAscii(s, t), 'Conversion is successful')