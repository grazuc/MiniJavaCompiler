///[Error:b|5]
///Test de una clase que define atributos void

class A{
    void b;
    int c;

    static void metodoEjemplo(){}
    static void main() {}
}

class B extends System {
    static void printIln(char p1) { }
    void debugPrint(int x) { }
}

class X implements J{
    void debugPrint(char x){}
}
class Y implements X{
    public Y(){
    }
}

interface J{}
class H extends B{
    static void debugPrint (char y){}
}