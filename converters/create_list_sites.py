if __name__=='__main__':

    file_name = 'map.dat'
    f = open(file_name,'r')
    coords= ', '.join([''.
        join(line.split()[0]+' '+line.split()[1]) for line in f])
    f.close()

    nf = open('locations.dat', 'w')
    nf.write(coords)
    nf.close()