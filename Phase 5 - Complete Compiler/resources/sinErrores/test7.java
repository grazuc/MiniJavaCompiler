///ObtengoobjetoB&ObtengoobjetoA&ObtengoobjetoB&9&exitosamente

class MainModule{
    static void main(){
        var a = new A();
        System.printIln(a.bMet().aMet().bMet().entero());

    }
}
class A{

    B bMet(){
        System.printSln("ObtengoobjetoB");
        return new B();
    }
}

class B{

    int varInstancia;

    A aMet(){
        System.printSln("ObtengoobjetoA");
        return new A();
    }

    int entero(){
        return 5+4;
    }
}