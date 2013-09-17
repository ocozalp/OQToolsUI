from xml.etree import ElementTree
from math import log, exp

def convertHazardMapToAscii(sourceFile, targetFile):
    tree = ElementTree.parse(sourceFile)

    with open(targetFile, 'w') as f:
        nodes = tree.findall('./oq:hazardMap/oq:node', {'oq': 'http://openquake.org/xmlns/nrml/0.4'})

        f.write('Lon\tLat\tIML\n')

        for node in nodes:
            f.write(node.attrib['lon'] + '\t' + node.attrib['lat'] + '\t' + node.attrib['iml'] + '\n')

        f.flush()


def convertHazardCurveToAscii(sourceFile, targetFile, returnPeriods=[72, 101, 475, 975, 2475, 4975, 10000]):
    tree = ElementTree.parse(sourceFile)

    with open(targetFile, 'w') as f:
        xmlNamespaces = {'oq': 'http://openquake.org/xmlns/nrml/0.4', 'gml': 'http://www.opengis.net/gml'}

        imlNode = tree.find('./oq:hazardCurves/oq:IMLs', xmlNamespaces)
        imlVals = [float(iml) for iml in imlNode.text.split(' ')]

        nodes = tree.findall('./oq:hazardCurves/oq:hazardCurve', xmlNamespaces)

        header = 'Lon\tLat'
        for returnPeriod in returnPeriods:
            header += '\t'
            header += str(returnPeriod)

        header += '\n'

        f.write(header)

        for node in nodes:
            coordinates = node.find('gml:Point/gml:pos', xmlNamespaces).text.split(' ')
            row = coordinates[0] + '\t' + coordinates[1]

            poes = [float(poe) for poe in node.find('oq:poEs', xmlNamespaces).text.split(' ')]

            for returnPeriod in returnPeriods:
                rrp = 1.0 / returnPeriod
                res = 0

                if imlVals[0] >= rrp:
                    res = imlVals[0]
                elif imlVals[len(imlVals)-1] <= rrp:
                    res = imlVals[len(imlVals)-1]
                else:
                    imlInd = 0
                    for i in xrange(1, len(poes)):
                        if poes[i] > rrp > poes[i-1] or poes[i-1] > rrp > poes[i]:
                            imlInd = i
                            break
                    x1 = c_log(imlVals[imlInd-1])
                    x2 = c_log(imlVals[imlInd])
                    y1 = c_log(poes[imlInd-1])
                    y2 = c_log(poes[imlInd])
                    y = log(rrp)

                    res = exp(((y - y1) * (x2 - x1)) / (y2 - y1) + x1)

                row += '\t' + str(res)

            f.write(row + '\n')

        f.flush()

def c_log(val):
    if val == 0:
        return 0.0

    return log(val)

def convertUhSpectraToAscii(sourceFile, targetFile):
    tree = ElementTree.parse(sourceFile)

    with open(targetFile, 'w') as f:
        xmlNamespaces = {'oq': 'http://openquake.org/xmlns/nrml/0.4', 'gml': 'http://www.opengis.net/gml'}

        periodNode = tree.find('./oq:uniformHazardSpectra/oq:periods', xmlNamespaces)
        periodVals = periodNode.text.split(' ')

        nodes = tree.findall('./oq:uniformHazardSpectra/oq:uhs', xmlNamespaces)

        header = 'Lon\tLat'
        for periodVal in periodVals:
            header += '\t'
            header += periodVal
        header += '\n'

        f.write(header)

        for node in nodes:
            coordinates = node.find('gml:Point/gml:pos', xmlNamespaces).text.split(' ')
            row = coordinates[0] + '\t' + coordinates[1]

            imls = node.find('oq:IMLs', xmlNamespaces).text.split(' ')
            for iml in imls:
                row += '\t' + iml

            f.write(row + '\n')

        f.flush()
