sudo apt-get install git
sudo apt-get install python-dev python-pip
sudo apt-get install libblas3gf libblas-doc libblas-dev
sudo apt-get install liblapack3gf liblapack-doc liblapack-dev
sudo apt-get install gfortran
sudo apt-get libgeos-c1

sudo pip install numpy
sudo pip install scipy
sudo pip install pyshp

git clone https://github.com/gem/oq-nrmllib.git
git clone https://github.com/gem/oq-hazardlib.git
git clone https://github.com/gem/oq-risklib.git

cd oq-nrmllib
sudo python setup.py install
cd oq-hazardlib
sudo python setup.py install
cd oq-risklib
sudo python setup.py install

git clone https://github.com/ocozalp/OQToolsUI.git
cd OQToolsUI
sudo python setup.py clean
sudo python setup.py install

cd ..
rm -rf oq-nrmllib hazardlib oq-risklib OQToolsUI
