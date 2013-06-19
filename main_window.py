__author__ = 'orhan'


from PyQt4 import QtGui as gui
from panels.converterwindow import ConverterWindow
from panels.inputfilegenwindow import InputFileGeneratorWindow


class MainWindow():
    def __init__(self):
        self.__mainWindow = gui.QMainWindow()
        self.initGui()
        
        self.showInputFileGeneratorWindow()

    def initGui(self):
        self.initMenu()
        
        self.__mainWindow.setGeometry(100, 100, 1024, 768)
        self.__mainWindow.setWindowTitle('OpenQuake Admin')

    def initMenu(self):
        menuBar = self.__mainWindow.menuBar()
        menu = menuBar.addMenu('Menu')

        action = gui.QAction('&Convert Source Models', self.__mainWindow)
        action.triggered.connect(self.showConverterWindow)
        menu.addAction(action)
        
        action = gui.QAction('&Generate Input Files', self.__mainWindow)
        action.triggered.connect(self.showInputFileGeneratorWindow)
        menu.addAction(action)
        
    def showConverterWindow(self):
        self.__mainWindow.setCentralWidget(ConverterWindow(parent=self.__mainWindow))
        
    def showInputFileGeneratorWindow(self):
        self.__mainWindow.setCentralWidget(InputFileGeneratorWindow(parent=self.__mainWindow))
    
    def show(self):
        self.__mainWindow.show()