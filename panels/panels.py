__author__ = 'orhan'


from PyQt4 import QtGui as gui


class BaseWindow(gui.QWidget):
    def __init__(self, parent=None):
        super(gui.QWidget, self).__init__(parent=parent)
        self.initGui()
    
    def handleFunction(self, lambdaFunc):
        try:
            return lambdaFunc()
        except Exception as exc:
            self._showMessage(exc.message)
    
    def _showMessage(self, text, title='Message'):
        gui.QMessageBox.about(self, title, text)