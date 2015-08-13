/*
 *     This file is part of MetaDataJobProperty.
 * 
 *     MetaDataJobProperty is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version
 *     Please consult http://www.gnu.org/licenses/agpl.txt or Licence.txt in this project
 *     Copyright @ 2015 Perf'Hector,  http://www.perfhector.info Email:94a027eda0c736c132b811136d8ea6c5-1341398@contact.gandi.net
 */
package info.perfhector.jenkinsci.plugins.metadatajobproperty;

import info.perfhector.jenkinsci.plugins.metadatajobproperty.MetadataJobProperty.MetaData;
import hudson.Extension;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.model.AbstractProject;
import hudson.model.Job;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 *
 * @author Cédric Levasseur cedric.levasseur@gmail.com
 */
public class MetadataJobProperty extends JobProperty<AbstractProject<?, ?>> {

    //the main checkbox in a job
    public static final String PROPERTY_NAME = "metadataJobProperty";
    
    //the map containing all the MetaData
    private final List<MetaData> listOfMetaData;

    @DataBoundConstructor
    public MetadataJobProperty(List<MetaData> listOfMetaData) {
         this.listOfMetaData=listOfMetaData;
    }

//    @Extension
//    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    /* getters */

    public List<MetaData> getListOfMetaData() {
        return listOfMetaData;
    }

    @Override
    public String toString() {
        StringBuilder result =new StringBuilder ("MetadataJobProperty{");
        for (MetaData m : listOfMetaData)
        {
            result.append(m.getKey()).append("=").append(m.getValue()).append(";");
        }
        result.append("}");
        return result.toString();
    }
    
    /**
     * Return the first metadata corresponding to the Key
     * @param key : the exact equals key
     * @return a MetaData object
     */
    
    public MetaData find (String key){
        for(MetaData m : listOfMetaData ){
            if(m.key.equals(key)){
                return m;
            }
        }
        return null;
    }
    
    private static final Logger LOGGER = Logger.getLogger(MetadataJobProperty.class.getName());
    
    /**
     * This class is used by the Jelly part
     */
    @Extension
    public static final class DescriptorImpl extends JobPropertyDescriptor {


        public DescriptorImpl() {
            super(MetadataJobProperty.class);
            load();
        }

        /**
         * @return key name used in the configuration Form.
         */
        public String getPropertyName() {
            return PROPERTY_NAME;
        }
        
        /**
         * Create the map from the http/json form
         * @param req
         * @param formData
         * @return
         * @throws hudson.model.Descriptor.FormException 
         */
        @Override
        public JobProperty<?> newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            
            if(formData == null || formData.isNullObject()) {
                LOGGER.log(Level.INFO,"formData is Null" );
                return null;
            }
            LOGGER.log(Level.INFO,"formData= "+formData.toString());
            Boolean checkbox = formData.getBoolean(getPropertyName());
            if(checkbox == null || checkbox == Boolean.FALSE ){
                return null;
            }           
            MetadataJobProperty jobProperty = req.bindJSON(MetadataJobProperty.class, formData);
            return jobProperty;
        }

        public String getDisplayName() {
            return "MetaDataJobProperty";
        }

        /**
         * Save globals plugin configuration from a http/json form
         * @param req
         * @param o
         * @return
         * @throws hudson.model.Descriptor.FormException 
         */
        @Override
        public boolean configure(StaplerRequest req, JSONObject o) throws FormException {
            LOGGER.log(Level.INFO,"configure with req="+req.toString());
            save();
            return true;
        }
        
        /**
         * Returns always true as it can be used in all types of jobs.
         *
         * @param jobType the job type to be checked if this property is applicable.
         * @return true
         */
        @Override
        public boolean isApplicable(Class<? extends Job> jobType) {
            return true;
        }


    }

    /**
     * Represents key/value entries defined by users in their jobs.
     */
    public static class MetaData implements Cloneable {

        private final String key;
        private final String value;

        @DataBoundConstructor
        public MetaData(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Object clone() {
            return new MetaData(key, value);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null) {
                return false;
            }
            if(getClass() != obj.getClass()) {
                return false;
            }
            final MetaData other = (MetaData) obj;
            return StringUtils.equals(key, other.getKey()) && StringUtils.equals(value, other.getValue());

        }

        public String getKey(){
            return key;
        }
        
        public String getValue(){
            return value;
        }
        

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + (this.key != null ? this.key.hashCode() : 0);
            hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
            return hash;
        }

        @Override
        public String toString() {
            return "MetaData{" + "key=" + key + ", value=" + value + '}';
        }
        
      
    }

}
