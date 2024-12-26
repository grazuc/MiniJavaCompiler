///8&1&exitosamente

class MainModule{
    static void main(){
        var a = new A();
        a.m1();
        System.printIln(a.entero);
    }
}
class A{
    int entero;

    void m1(){
        entero = 1;
        var b = new B();
        b.mB();
        System.printIln(entero+b.intB);
    }
}

class B{
    int intB;

    void mB(){
        intB = 7;
    }
}