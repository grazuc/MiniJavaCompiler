///m1enA&m2enA&m3enB& &m1enB&m2enB&m3enB&exitosamente


class MainModule{

    static void main() {

        var a = new A();
        a.m1();
        a.m2();
        a.m3();

        System.println();

        var bAux = new B();
        bAux.m1();
        bAux.m2();
        bAux.m3();
    }
}

class A extends B {

    void m1(){
        System.printSln("m1enA");
    }

    void m2(){
        System.printSln("m2enA");
    }
}

class B implements X{

    void m1(){
        System.printSln("m1enB");
    }

    void m2(){
        System.printSln("m2enB");
    }

    void m3(){
        System.printSln("m3enB");
    }
}

interface X extends Y{
    void m1();
}

interface Y extends W{
    void m2();
}

interface W {
    void m3();
}
