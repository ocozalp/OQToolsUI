from openquake.nrmllib.hazard.writers import SourceModelXMLWriter
from converters.shapefileconverter import ShapeFileConverter
from xml.etree import ElementTree
from common.xml_utils import write_xml

import os


def convert(sourceFileName, targetFileName, sourceType,
            nameMappings={'A': 'aGRval', 'B': 'bGRval', 'ID': 'EMME_IDAS', 'NAME': 'CODE'}):
    deserializedShps = convertShapeFileToNrml(sourceFileName, targetFileName, nameMappings, sourceType)

    fileName, ext = os.path.splitext(targetFileName)
    sourceModels = list()
    for i in xrange(len(deserializedShps)):
        deserializedShp = deserializedShps[i][0]
        shapeFileName = fileName + '_' + str(i) + ext
        writer = SourceModelXMLWriter(shapeFileName)
        writer.serialize(deserializedShp)
        sourceModels.append((os.path.basename(shapeFileName), deserializedShps[i][1]))

    writeSourceModelTree(os.path.join(os.path.dirname(targetFileName), 'source_model_logic_tree.xml'), sourceModels)


def convertShapeFileToNrml(sourceFileName, targetFileName, nameMappings, sourceType):
    ind = sourceFileName.rindex('.')
    sourceFileName = sourceFileName[:ind]

    s = ShapeFileConverter(sourceFileName, targetFileName, nameMappings)
    return s.parse(sourceType)


def writeSourceModelTree(targetFilePath, sourceModels):
    nrml_node = ElementTree.Element('nrml')
    nrml_node.set('xmlns', 'http://openquake.org/xmlns/nrml/0.4')

    logic_tree_node = ElementTree.SubElement(nrml_node, 'logicTree')
    logic_tree_node.set('logicTreeID', 'lt1')

    logic_tree_branching_level_node = ElementTree.SubElement(logic_tree_node, 'logicTreeBranchingLevel')
    logic_tree_branching_level_node.set('branchingLevelID', 'bl1')

    logic_tree_branch_set_node = ElementTree.SubElement(logic_tree_branching_level_node, 'logicTreeBranchSet')
    logic_tree_branch_set_node.set('uncertaintyType', 'sourceModel')
    logic_tree_branch_set_node.set('branchSetID', 'bs1')

    for i in xrange(len(sourceModels)):
        logic_tree_branch_node = ElementTree.SubElement(logic_tree_branch_set_node, 'logicTreeBranch')
        logic_tree_branch_node.set('branchID', 'b'+str(i+1))

        uncertainty_mode_node = ElementTree.SubElement(logic_tree_branch_node, 'uncertaintyModel')
        uncertainty_mode_node.text = '../../SourceModels/' + sourceModels[i][0]

        uncertainty_weight_node = ElementTree.SubElement(logic_tree_branch_node, 'uncertaintyWeight')
        uncertainty_weight_node.text = str(sourceModels[i][1])

    tree = ElementTree.ElementTree(nrml_node)

    write_xml(tree, targetFilePath)