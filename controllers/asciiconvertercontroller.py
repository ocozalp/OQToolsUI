from xml.etree import ElementTree

def convertHazardMapToAscii(sourceFile, targetFile):
    tree = ElementTree.parse(sourceFile)

    with open(targetFile, 'w') as f:
        nodes = tree.findall('./oq:hazardMap/oq:node', {'oq': 'http://openquake.org/xmlns/nrml/0.4'})

        f.write('Lon\tLat\tIML\n')

        for node in nodes:
            f.write(node.attrib['lon'] + '\t' + node.attrib['lat'] + '\t' + node.attrib['iml'] + '\n')

        f.flush()


def convertHazardCurveToAscii(sourceFile, targetFile):
    tree = ElementTree.parse(sourceFile)

    with open(targetFile, 'w') as f:
        xmlNamespaces = {'oq': 'http://openquake.org/xmlns/nrml/0.4', 'gml': 'http://www.opengis.net/gml'}

        imlNode = tree.find('./oq:hazardCurves/oq:IMLs', xmlNamespaces)
        imlVals = imlNode.text.split(' ')

        nodes = tree.findall('./oq:hazardCurves/oq:hazardCurve', xmlNamespaces)

        header = 'Lon\tLat'
        for imlVal in imlVals:
            header += '\t'
            header += imlVal
        header += '\n'

        f.write(header)

        for node in nodes:
            coordinates = node.find('gml:Point/gml:pos', xmlNamespaces).text.split(' ')
            row = coordinates[0] + '\t' + coordinates[1]

            poes = node.find('oq:poEs', xmlNamespaces).text.split(' ')
            for poe in poes:
                row += '\t' + poe


            f.write(row + '\n')

        f.flush()


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
