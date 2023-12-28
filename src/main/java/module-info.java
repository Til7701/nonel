module noel {
    requires static lombok;
    requires javafx.graphics;
    requires javafx.controls;

    exports de.holube.noel;
    opens de.holube.noel;
}
