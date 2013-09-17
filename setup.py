from setuptools import setup

PY_MODULES = ['oqtools.main']

setup(
    entry_points={
        "console_scripts": [
            "OQToolsUI = oqtools.main:main",
            ],
        },
    name='OQToolsUI',
    version='1.0.0',
    packages=['common', 'ui', 'converters', 'controllers', 'oqtools'],
    url='http://github.com/ocozalp/OQToolsUI',
    license='Apache License Version 2.0',
    author='ocozalp',
    author_email='ozalp.orhan@gmail.com',
    description='UI for openquake'
)
