package ch.valtech.kubernetes.microservice.cluster.releasetoggle.annotation.featuregroup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.togglz.core.annotation.FeatureGroup;
import org.togglz.core.annotation.Label;

@FeatureGroup
@Label("Bugfix")
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bugfix {

}
