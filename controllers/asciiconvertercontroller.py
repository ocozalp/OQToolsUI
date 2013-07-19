from xml.etree import ElementTree


def convertHazardMapToAscii(sourceFile, targetFile):
    tree = ElementTree.parse(sourceFile)

    with open(targetFile, 'w') as f:
        nodes = tree.findall('./oq:hazardMap/oq:node', {'oq': 'http://openquake.org/xmlns/nrml/0.4'})

        f.write('Lon\tLat\tIML\n')

        for node in nodes:
            f.write(node.attrib['lon'] + '\t' + node.attrib['lat'] + '\t' + node.attrib['iml'] + '\n')

        f.flush()