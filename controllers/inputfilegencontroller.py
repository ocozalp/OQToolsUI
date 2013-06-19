__author__ = 'orhan'


import os


def save(parameters, targetFile):
    f = open(targetFile, 'w')
    for key in parameters:
        line = key + '=' + parameters[key]
        f.write(line + '\n')

    f.flush()
    f.close()


def load(sourceFile):
    f = open(sourceFile, 'r')
    parameters = {}
    for line in f.readlines():
        index = line.index('=')
        if index >= 0:
            parameters[line[:index].strip()] = line[index+1:].strip()
    
    f.close()
    return parameters


def execute(parameters, targetDir, separate):
    if 'name' not in parameters or len(parameters['name'].strip()) == 0:
        raise Exception('Name parameter is empty')

    runName = parameters['name'].strip()
    newDir = os.path.join(targetDir, runName)

    if not os.path.exists(newDir):
        os.makedirs(newDir)

    if separate:
        for gmpe in parameters['gmpes']:
            gmpeDir = os.path.join(newDir, gmpe.name)
            if not os.path.exists(gmpeDir):
                os.makedirs(gmpeDir)

            jobIniContent = getJobIniContent(parameters, 'output/' + runName + '/' + gmpe.name)
            writeGmpeLogicTree(os.path.join(gmpeDir, 'gmpe_logic_tree.xml'), [gmpe])
            writeJobIni(os.path.join(gmpeDir, 'job.ini'), jobIniContent)
    else:
        jobIniContent = getJobIniContent(parameters, 'output/'+runName)
        writeGmpeLogicTree(os.path.join(newDir, 'gmpe_logic_tree.xml'), parameters['gmpes'])
        writeJobIni(os.path.join(newDir, 'job.ini'), jobIniContent)


def getJobIniContent(parameters, outDir):
    parentDir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    jobIniFile = os.path.join(parentDir, 'job.ini.template')
    intensityLevelsFile = os.path.join(parentDir, 'intensityLevels.txt')

    with open(jobIniFile, 'r') as f:
        jobIniTemplate = f.read()

    with open(intensityLevelsFile, 'r') as f:
        intensityLevels = f.read()

    for prm in parameters:
        key = '[['+str(prm)+']]'

        if key in jobIniTemplate:
            jobIniTemplate = jobIniTemplate.replace(key, parameters[prm])

    if 'periods' in parameters:
        periodStr = '{'
        periodList = list()

        for period in parameters['periods'].split(' '):
            period = float(period.strip())
            if period == 0.0:
                periodStr = '"PGA"'
            else:
                periodStr = '"SA(' + str(period) + ')"'

            periodStr += ': ' + intensityLevels
            periodList.append(periodStr)

        periodStr += ','.join(periodList)
        periodStr += '}'

        jobIniTemplate = jobIniTemplate.replace('[[intensity_levels]]', periodStr)

    jobIniTemplate = jobIniTemplate.replace('[[export_dir]]', outDir)
    return jobIniTemplate


def writeJobIni(filePath, jobIniContent):
    f = open(filePath, 'w')
    f.write(jobIniContent)
    f.flush()
    f.close()


def writeGmpeLogicTree(targetFilePath, gmpes):
    template = '''
        <?xml version="1.0" encoding="UTF-8"?>
        <nrml xmlns:gml="http://www.opengis.net/gml"
              xmlns="http://openquake.org/xmlns/nrml/0.4">
            <logicTree logicTreeID='lt1'>
                <logicTreeBranchingLevel branchingLevelID="bl1">
                    <logicTreeBranchSet uncertaintyType="gmpeModel" branchSetID="bs1"
                            applyToTectonicRegionType="Active Shallow Crust">
                        [[branches]]
                    </logicTreeBranchSet>
                </logicTreeBranchingLevel>
            </logicTree>
        </nrml>
    '''

    branches = ''
    for i in range(len(gmpes)):
        branch = '''
                <logicTreeBranch branchID="b[[i]]">
                            <uncertaintyModel>[[gmpeName]]</uncertaintyModel>
                            <uncertaintyWeight>[[weight]]</uncertaintyWeight>
                </logicTreeBranch>
        '''

        branch = branch.replace('[[i]]', str(i+1))
        branch = branch.replace('[[gmpeName]]', gmpes[i].name)
        branch = branch.replace('[[weight]]', str(gmpes[i].weight))

        branches += branch

    template = template.replace('[[branches]]', branches)
    f = open(targetFilePath, 'w')
    f.write(template)
    f.write('\n')

    f.flush()
    f.close()