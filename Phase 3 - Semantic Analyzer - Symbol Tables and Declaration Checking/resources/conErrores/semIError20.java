///[Error:B|14]

//test de una clase que implementa a una interface pero no implementa todos sus metodos


interface B {

    void metodo_1();

    void metodo_2();

}

class A implements B {

    void metodo_1() {

    }

    static void main() {}


}