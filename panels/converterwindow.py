__author__ = 'orhan'


from PyQt4 import QtGui as gui
from panels import BaseWindow
from widgets import BrowseFileText, NamedTextArea
from controllers.convertercontroller import convert


class ConverterWindow(BaseWindow):

    def __init__(self, parent=None):
        super(BaseWindow, self).__init__(parent=parent)
        self.initGui()
    
    def initGui(self):
        self.sourceFile = BrowseFileText(self, isDir=False)
        self.sourceFile.initGui(self, 'Source File Name', 10, 10)
        
        self.targetFile = BrowseFileText(self, isDir=False)
        self.targetFile.initGui(self, 'Target File Name', 10, 60)

        label = gui.QLabel('Parameters', self)
        label.setGeometry(10, 110, 150, 30)

        self.aValPrm = NamedTextArea(self)
        self.aValPrm.initGui(self, 'A', 10, 160)
        self.aValPrm.setText('aGRval')

        self.bValPrm = NamedTextArea(self)
        self.bValPrm.initGui(self, 'B', 10, 210)
        self.bValPrm.setText('bGRval')

        self.idPrm = NamedTextArea(self)
        self.idPrm.initGui(self, 'ID', 10, 260)
        self.idPrm.setText('EMME_IDAS')

        self.namePrm = NamedTextArea(self)
        self.namePrm.initGui(self, 'Name', 10, 310)
        self.namePrm.setText('CODE')

        converter = gui.QPushButton('Convert', self)
        converter.setGeometry(410, 350, 120, 40)
        converter.clicked.connect(self.__convertFiles)
        
    def __convertFiles(self):
        sourceFileName = self.sourceFile.getSelectedFile()
        targetFileName = self.targetFile.getSelectedFile()

        params = dict()
        params['A'] = self.aValPrm.getText()
        params['B'] = self.bValPrm.getText()
        params['ID'] = self.idPrm.getText()
        params['NAME'] = self.namePrm.getText()

        result = self.handleFunction(lambda: convert(sourceFileName, targetFileName, params))
        
        if result:
            self._showMessage('Source model file is generated', 'Success!')