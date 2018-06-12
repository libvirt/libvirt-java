# libvirt-java

The `libvirt` JNA bindings. Forked from `http://libvirt.org/git/?p=libvirt-java.git`.

### Build

After running the `package` command the JAR will be located in the `target` directory.

```
mvn clean package
```

## Running the tests

```
mvn clean test
```

## Releasing it!

Two steps;
 1. Checkout a release branch
 2. Run the mvn release command

```
git checkout -b release/x.x.x
mvn release:prepare release:perform -Darguments="-Dmaven.javadoc.skip=true"
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
