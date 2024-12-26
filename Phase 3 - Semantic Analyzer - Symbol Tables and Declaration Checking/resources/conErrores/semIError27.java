///[Error:Z|19]
//test de una una clase  que implementa una interface que extiende a otras dos interfaces y no implementa todos los metodos


interface X {

    void metodox();
}

interface Y {
    void metodoy();
}

interface Z extends Y{

}


class A implements Z {


    static void main() {}
}