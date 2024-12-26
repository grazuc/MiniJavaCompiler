///[Error:ClaseNoDeclarada|5]

class A{

    ClaseNoDeclarada atributoClaseNoDeclarada;

    void metodoConAtributoClaseNoDeclarada(ClaseNoDeclarada atributoClaseNoDeclarada){}

    public A(ClaseNoDeclarada atributoClaseNoDeclarada){}
    int mismoNombreClase;
    char mismoNombreClase;
    boolean mismoNombreHerenciaSimple;
    int mismoNombreHerenciaMultiple;

    int mismoNombreMetodoEnUnaClase(){}
    boolean mismoNombreMetodoEnUnaClase(){}

    int mismoNombreMetodoHerenciaSimple(){}

    char mismoNombreMetodoHerenciaMultiple(){}

    public C(int x){}



}

class B extends A{
    public B(int x){}
    boolean mismoNombreHerenciaSimple;

    char mismoNombreMetodoHerenciaSimple(){}
}

class C extends B{
    public C(int x){}
    boolean mismoNombreHerenciaMultiple;

    int mismoNombreMetodoHerenciaMultiple(){}
}