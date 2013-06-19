__author__ = 'orhan'


from openquake.nrmllib.hazard.writers import SourceModelXMLWriter
from converters.shapefileconverter import ShapeFileConverter
import os


def convert(sourceFileName, targetFileName,
            nameMappings={'A': 'aGRval', 'B': 'bGRval', 'ID': 'EMME_IDAS', 'NAME': 'CODE'}):
    deserializedShps = convertShapeFileToNrml(sourceFileName, targetFileName, nameMappings)

    fileName, ext = os.path.splitext(targetFileName)
    sourceModels = list()
    for i in xrange(len(deserializedShps)):
        deserializedShp = deserializedShps[i][0]
        shapeFileName = fileName + '_' + str(i) + ext
        writer = SourceModelXMLWriter(shapeFileName)
        writer.serialize(deserializedShp)
        sourceModels.append((os.path.basename(shapeFileName), deserializedShps[i][1]))

    writeSourceModelTree(os.path.join(os.path.dirname(targetFileName), 'source_model_logic_tree.xml'), sourceModels)

    return True


def convertShapeFileToNrml(sourceFileName, targetFileName, nameMappings):
    ind = sourceFileName.rindex('.')
    sourceFileName = sourceFileName[:ind]

    s = ShapeFileConverter(sourceFileName, targetFileName, nameMappings)
    return s.parse()


def writeSourceModelTree(targetFilePath, sourceModels):
    template = '''<?xml version="1.0" encoding="UTF-8"?>
<nrml xmlns:gml="http://www.opengis.net/gml"
      xmlns="http://openquake.org/xmlns/nrml/0.4">
    <logicTree logicTreeID="lt1">
        <logicTreeBranchingLevel branchingLevelID="bl1">
            <logicTreeBranchSet uncertaintyType="sourceModel" branchSetID="bs1">
                [[branches]]
            </logicTreeBranchSet>
        </logicTreeBranchingLevel>
    </logicTree>
</nrml>
        '''

    branches = ''
    branchTemplate = '''
                <logicTreeBranch branchID="b[[index]]">
                    <uncertaintyModel>../../SourceModels/[[sourceModelName]]</uncertaintyModel>
                    <uncertaintyWeight>[[weight]]</uncertaintyWeight>
                </logicTreeBranch>
        '''

    for i in xrange(len(sourceModels)):
        branchTemplateStr = branchTemplate.replace('[[index]]', str(i+1))
        branchTemplateStr = branchTemplateStr.replace('[[sourceModelName]]', sourceModels[i][0])
        branchTemplateStr = branchTemplateStr.replace('[[weight]]', str(sourceModels[i][1]))
        branches = "\n".join([branches, branchTemplateStr])

    template = template.replace('[[branches]]', branches)
    f = open(targetFilePath, 'w')
    f.write(template)
    f.write('\n')

    f.flush()
    f.close()