plugins {
    id("org.openjfx.javafxplugin")
}

javafx {
    version = "23"
    modules("javafx.controls", "javafx.fxml", "javafx.media", "javafx.swing")
}

dependencies {
    implementation(project(":shared"))

    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.3")
    implementation("org.kordamp.ikonli:ikonli-core:12.4.0")
    implementation("org.kordamp.ikonli:ikonli-javafx:12.4.0")
    implementation("org.kordamp.ikonli:ikonli-fontawesome5-pack:12.4.0")
    implementation("org.kordamp.ikonli:ikonli-materialdesign2-pack:12.4.0")
    implementation("org.jfree:jfreechart:1.5.5")
    implementation("org.jfree:jfreechart-fx:1.0.1")

    testImplementation("org.junit.jupiter:junit-jupiter")
}
