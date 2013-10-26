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
    version='1.0.1',
    packages=find_packages(),
    package_data={'oqtools': ['intensityLevels.txt', 'job.ini.template']},
    classifiers=(
        'Classifier: Development Status :: 4 - Beta',
        'Classifier: Environment :: X11 Applications :: Qt',
        'Classifier: Intended Audience :: Education',
        'Classifier: Intended Audience :: Science/Research',
        'Classifier: License :: OSI Approved :: GNU Affero General Public License v3',
        'Classifier: Natural Language :: English',
        'Classifier: Operating System :: POSIX :: Linux',
        'Classifier: Programming Language :: Python :: 2.7',
        'Classifier: Programming Language :: Python :: Implementation :: CPython',
        'Classifier: Topic :: Education',
        'Classifier: Topic :: Scientific/Engineering'
    ),
    url='http://github.com/ocozalp/OQToolsUI',
    license='GNU Affero General Public License v3',
    author='Orhan Can Ozalp',
    author_email='ozalp.orhan@gmail.com',
    description='UI for openquake'
)
