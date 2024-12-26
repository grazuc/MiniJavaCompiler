///9&exitosamente

class MainModule{
    static void main(){
        var a = new A();
        var b = a.bMet();
        System.printIln(b.entero());
    }
}
class A{

    B bMet(){
        return new B();
    }
}

class B{

    int entero(){
        return 5+4;
    }
}