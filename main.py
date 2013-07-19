import sys
from main_window import MainWindow
from PyQt4 import QtGui as gui


def main():
    app = gui.QApplication(sys.argv)
    mw = MainWindow()
    mw.show()
    sys.exit(app.exec_())


if __name__ == '__main__':
    main()