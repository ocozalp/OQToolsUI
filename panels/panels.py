from PyQt4 import QtGui as gui


class BaseWindow(gui.QWidget):
    def __init__(self, parent=None):
        super(gui.QWidget, self).__init__(parent=parent)
        self.initGui()
    
    def callFunction(self, lambdaFunc):
        try:
            return lambdaFunc()
        except Exception as exc:
            self.showMessage(exc.message)
    
    def showMessage(self, text, title='Message'):
        messageBox = gui.QMessageBox()
        messageBox.about(self, title, text)