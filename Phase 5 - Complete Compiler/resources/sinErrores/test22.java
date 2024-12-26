///m1 en B&m1 en C&exitosamente


class A{
    static void main(){
        getB().m1();
        getC().m1();
    }

    static I getB(){
        return new B();
    }

    static I getC(){
        return new C();
    }
}

interface I{
    void m1();
}

class B implements I{
    void m1(){
        System.printSln("m1 en B");
    }
}

class C implements I{
    void m1(){
        System.printSln("m1 en C");
    }
}