from setuptools import setup, find_packages

PY_MODULES = ['oqtools.main']

setup(
    entry_points={
        "console_scripts": [
            "OQToolsUI = oqtools.main:main",
            ],
        },
    install_requires=['openquake.hazardlib<=0.11.0', 'openquake.nrmllib<=0.4.5', 'pyshp'],
    name='OQToolsUI',
    version='1.0.0',
    packages=find_packages(),
    package_data={'oqtools': ['intensityLevels.txt', 'job.ini.template']},
    url='http://github.com/ocozalp/OQToolsUI',
    license='Apache License Version 2.0',
    author='Orhan Can Ozalp',
    author_email='ozalp.orhan@gmail.com',
    description='UI for openquake'
)
