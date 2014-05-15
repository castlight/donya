donya
=====

Donya (the Javanese word for "the world") is a declarative internationalization framework for Java.

You use annotations in a class to identify fields which need to be localized.  

The current implementation uses these annotations to add the localized text when serializing an object
into JSON using the Jackson JSON library.  However, other implementations can be implemented that use
other mechanisms to automatically format the localized text.

You can build using standard maven.  There is also an Eclipse project file.

See the org.donyasource.dony.examples directory for some examples of how this is used.

Feedback and contributions welcome!

