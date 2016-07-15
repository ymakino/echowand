package echowand.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Serivceで提供するdo*メソッドを生成
 * @author ymakino
 */
public class ServiceDoMethodsGenerator {
    public static class Rule {
        private String type;
        private String paramName;
        private String converted;
        private boolean throwException;
        private List<String> conversionLines;
        
        public Rule (String type, String paramName, String converted) {
            this.type = type;
            this.paramName = paramName;
            this.converted = converted;
            this.throwException = true;
            this.conversionLines = new LinkedList<String>();
        }
        
        public Rule (String type, String paramName, String converted, List<String> conversionLines) {
            this.type = type;
            this.paramName = paramName;
            this.converted = converted;
            this.throwException = true;
            this.conversionLines = new LinkedList<String>(conversionLines);
        }
        
        public Rule (String type, String paramName, String converted, String... conversionLines) {
            this.type = type;
            this.paramName = paramName;
            this.converted = converted;
            this.throwException = true;
            this.conversionLines = new LinkedList<String>(Arrays.asList(conversionLines));
        }
        
        public Rule (String type, String paramName, boolean throwException, String converted) {
            this.type = type;
            this.paramName = paramName;
            this.converted = converted;
            this.throwException = throwException;
            this.conversionLines = new LinkedList<String>();
        }
        
        public Rule (String type, String paramName, String converted, boolean throwException, List<String> conversionLines) {
            this.type = type;
            this.paramName = paramName;
            this.converted = converted;
            this.throwException = throwException;
            this.conversionLines = new LinkedList<String>(conversionLines);
        }
        
        public Rule (String type, String paramName, String converted, boolean throwException, String... conversionLines) {
            this.type = type;
            this.paramName = paramName;
            this.converted = converted;
            this.throwException = throwException;
            this.conversionLines = new LinkedList<String>(Arrays.asList(conversionLines));
        }
        
        public String getType() {
            return type;
        }
        
        public String getParamName() {
            return paramName;
        }
        
        public String getConverted() {
            return converted;
        }
        
        public boolean existsConversionLines() {
            return !conversionLines.isEmpty();
        }
        
        public int countConversionLines() {
            return conversionLines.size();
        }
        
        public String getConversionLine(int index) {
            return conversionLines.get(index);
        }
        
        public boolean mayThrowException() {
            return throwException;
        }
    }
    
    public static class Parameter {
        private List<Rule> rules;
        
        public Parameter() {
            rules = new LinkedList<Rule>();
        }
        
        public Parameter with(Rule rule) {
            rules.add(rule);
            return this;
        }
        
        public int countRules() {
            return rules.size();
        }
        
        public Rule getRule(int index) {
            return rules.get(index);
        }
    }
        
    public static interface Filter {
        public boolean filter(List<Rule> rules);
    }
    
    public static class MethodGenerator {
        private String name;
        private String returnType;
        private String returnName;
        private List<Parameter> parameters;
        private String indent;
        private List<Filter> filters;
        
        public static class DefaultFilter implements Filter {
            @Override
            public boolean filter(List<Rule> rules) {
                for (int i=0; i<rules.size(); i++) {
                    Rule rule = rules.get(i);
                    if (rule.getParamName() == null || !rule.getParamName().equals(rule.getConverted())) {
                        return true;
                    }
                }
                
                return false;
            }
        }
        
        public MethodGenerator(String name, String returnType, List<Parameter> parameters) {
            this.name = name;
            this.returnType = returnType;
            this.returnName = Character.toLowerCase(returnType.charAt(0)) + returnType.substring(1);
            this.parameters = new LinkedList<Parameter>(parameters);
            indent = "    ";
            filters = new LinkedList<Filter>();
            filters.add(new DefaultFilter());
        }
        
        public MethodGenerator(String name, String returnType, Parameter... parameters) {
            this.name = name;
            this.returnType = returnType;
            this.returnName = Character.toLowerCase(returnType.charAt(0)) + returnType.substring(1);
            this.parameters = new LinkedList<Parameter>(Arrays.asList(parameters));
            indent = "    ";
            filters = new LinkedList<Filter>();
            filters.add(new DefaultFilter());
        }
        
        public MethodGenerator withFilter(Filter filter) {
            filters.add(filter);
            return this;
        }
        
        public boolean filter(List<Rule> rules) {
            for (Filter filter : filters) {
                if (!filter.filter(rules)) {
                    return false;
                }
            }
            
            return true;
        }
        
        private List<List<Rule>> generatePartialRules(int index) {
            
            if (parameters.size() == index) {
                List<List<Rule>> ret = new LinkedList<List<Rule>>();
                ret.add(new LinkedList<Rule>());
                return ret;
            }
            
            Parameter headParameter = parameters.get(index);
            List<List<Rule>> tailRuless = generatePartialRules(index+1);
            
            List<List<Rule>> resultRules = new LinkedList<List<Rule>>();
            for (List<Rule> tailRules : tailRuless) {
                for (int i=0; i<headParameter.countRules(); i++) {
                    List<Rule> rules = new LinkedList<Rule>(tailRules);
                    rules.add(headParameter.getRule(i));
                    resultRules.add(rules);
                }
            }
            
            return resultRules;
        }
        
        private List<List<Rule>> generateAllRules() {
            List<List<Rule>> ruless = generatePartialRules(0);
            List<List<Rule>> retRuless = new LinkedList<List<Rule>>();
            
            for (List<Rule> rules : ruless) {
                LinkedList<Rule> retRules = new LinkedList<Rule>();
                
                for (int i=rules.size()-1; 0<=i; i--) {
                    retRules.add(rules.get(i));
                }
                
                if (filter(retRules)) {
                    retRuless.add(retRules);
                }
            }
            
            return retRuless;
        }
        
        private String generateFirstLine(List<Rule> rules, boolean isInterface) {
            
            StringBuilder builder = new StringBuilder();
            builder.append(indent).append("public ").append(returnType);
            builder.append(" ").append(name).append("(");

            boolean isHead = true;
            boolean mayThrownException = false;
            
            for (int i=0; i<rules.size(); i++) {

                Rule rule = rules.get(i);
                
                mayThrownException |= rule.mayThrowException();
                
                if (rule.getParamName() == null) {
                    continue;
                }
                
                if (isHead) {
                    isHead = false;
                } else {
                    builder.append(", ");
                }
                
                builder.append(rule.type).append(" ").append(rule.paramName);
            }
            
            builder.append(")");

            if (mayThrownException) {
                builder.append(" throws SubnetException");
            }
            
            if (isInterface) {
                builder.append(";\n");
            } else {
                builder.append(" {\n");
            }

            return builder.toString();
        }
        
        private String generateEnterLog(List<Rule> rules) {
            StringBuilder builder = new StringBuilder();
            
            builder.append(indent).append(indent);
            builder.append("LOGGER.entering(CLASS_NAME, ");
            builder.append('"').append(name).append('"');
            
            LinkedList<String> params = new LinkedList<String>();
            
            for (Rule rule : rules) {
                if (rule.getParamName() != null) {
                    params.add(rule.getParamName());
                }
            }
            
            if (params.size() == 1) {
                builder.append(", ").append(params.get(0));
            } else if (params.size() > 1){
                builder.append(", new Object[]{");
                for (int i=0; i<params.size(); i++) {
                    if (i!=0) {builder.append(", ");}
                    builder.append(params.get(i));
                }
                builder.append("}");
            }
            
            builder.append(");\n");
            
            return builder.toString();
        }
        
        private String generateReturn() {
            return indent + indent + "return " + returnName + ";\n";
        }
        
        private String generateLastLine() {
            return indent + "}\n";
        }
        
        private String generateConvLines(List<Rule> rules) {
            StringBuilder builder = new StringBuilder();
            
            for (Rule rule: rules) {
                for (int i=0; i<rule.countConversionLines(); i++) {
                    builder.append(indent).append(indent);
                    builder.append(rule.getConversionLine(i)).append("\n");
                }
            }
            
            return builder.toString();
        }
        
        private String generateCall(List<Rule> rules) {
            StringBuilder builder = new StringBuilder();
            
            builder.append(indent);
            builder.append(indent);
            
            builder.append(returnType).append(" ");
            builder.append(returnName).append(" = ");
            builder.append(name).append("(");
            
            boolean isHead = true;
            for (int i=0; i<rules.size(); i++) {
                if (rules.get(i).getConverted() == null) {
                    continue;
                }
                
                if (isHead) {
                    isHead = false;
                } else {
                    builder.append(", ");
                }
                
                builder.append(rules.get(i).getConverted());
            }
            
            builder.append(");\n");
            
            return builder.toString();
        }
        
        private String generateInterface(List<Rule> rules) {
            StringBuilder builder = new StringBuilder();
            
            builder.append(generateFirstLine(rules, true));
            
            return builder.toString();
        }
        
        private String generateMethod(List<Rule> rules) {
            StringBuilder builder = new StringBuilder();
            
            builder.append(generateFirstLine(rules, false));
            builder.append(generateEnterLog(rules));
            
            builder.append(indent).append(indent).append("\n");
            builder.append(generateConvLines(rules));
            builder.append(generateCall(rules));
            
            builder.append(indent).append(indent).append("\n");
            builder.append(generateExitLog(rules, returnName));
            builder.append(generateReturn());
            builder.append(generateLastLine());
            
            return builder.toString();
        }
        
        private String generateExitLog(List<Rule> rules, String returnName) {
            StringBuilder builder = new StringBuilder();
            
            builder.append(indent).append(indent);
            builder.append("LOGGER.exiting(CLASS_NAME, ");
            builder.append('"').append(name).append('"');
            
            if (returnName != null){
                builder.append(", ");
                builder.append(returnName);
            }
            
            builder.append(");\n");
            
            return builder.toString();
        }
        
        public List<String> generateInterfaces() {
            List<List<Rule>> generated = generateAllRules();
            
            List<String> ret = new LinkedList<String>();
            
            for (List<Rule> rule : generated) {
                ret.add(generateInterface(rule));
            }
            
            return ret;
        }
        
        public List<String> generateMethods() {
            List<List<Rule>> generated = generateAllRules();
            
            List<String> ret = new LinkedList<String>();
            
            for (List<Rule> rule : generated) {
                ret.add(generateMethod(rule));
            }
            
            return ret;
        }
    }
    
    public MethodGenerator createDoGetGenerator() {
        Parameter p0 = new Parameter()
            .with(new Rule("Node", "node", "node"))
            .with(new Rule("NodeInfo", "nodeInfo", "getRemoteNode(nodeInfo)"));
        
        Parameter p1 = new Parameter()
            .with(new Rule("EOJ", "eoj", "eoj"))
            .with(new Rule("ClassEOJ", "ceoj", "ceoj.getAllInstanceEOJ()"));
        
        Parameter p2 = new Parameter()
            .with(new Rule("EPC", "epc", "toList(epc)"))
            .with(new Rule("List<EPC>", "epcs", "epcs"));
        
        Parameter p3 = new Parameter()
            .with(new Rule("int", "timeout", "timeout"));
        
        Parameter p4 = new Parameter()
            .with(new Rule("GetListener", "getListener", "getListener"))
            .with(new Rule("GetListener", null, "null"));
        
        return new MethodGenerator("doGet", "GetResult", p0, p1, p2, p3, p4);
    }
    
    public MethodGenerator createDoSetGenerator1() {
        Parameter p0 = new Parameter()
            .with(new Rule("Node", "node", "node"))
            .with(new Rule("NodeInfo", "nodeInfo", "getRemoteNode(nodeInfo)"));
        
        Parameter p1 = new Parameter()
            .with(new Rule("EOJ", "eoj", "eoj"))
            .with(new Rule("ClassEOJ", "ceoj", "ceoj.getAllInstanceEOJ()"));
        
        Parameter p2 = new Parameter()
            .with(new Rule("EPC", "epc", "properties",
                    "LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();",
                    "properties.add(new Pair<EPC, Data>(epc, data));"));
        
        Parameter p3 = new Parameter()
            .with(new Rule("Data", "data", null));
        
        Parameter p4 = new Parameter()
            .with(new Rule("int", "timeout", "timeout"));
        
        Parameter p5 = new Parameter()
            .with(new Rule("boolean", "responseRequired", "responseRequired"))
            .with(new Rule("boolean", null, "false"));
        
        Parameter p6 = new Parameter()
            .with(new Rule("SetListener", "setListener", "setListener"))
            .with(new Rule("SetListener", null, "null"));
        
        return new MethodGenerator("doSet", "SetResult", p0, p1, p2, p3, p4, p5, p6);
    }
    
    public MethodGenerator createDoSetGenerator2() {
        Parameter p0 = new Parameter()
            .with(new Rule("Node", "node", "node"))
            .with(new Rule("NodeInfo", "nodeInfo", "getRemoteNode(nodeInfo)"));
        
        Parameter p1 = new Parameter()
            .with(new Rule("EOJ", "eoj", "eoj"))
            .with(new Rule("ClassEOJ", "ceoj", "ceoj.getAllInstanceEOJ()"));
        
        Parameter p2 = new Parameter()
            .with(new Rule("List<Pair<EPC, Data>>", "properties", "properties"));
        
        Parameter p3 = new Parameter()
            .with(new Rule("int", "timeout", "timeout"));
        
        Parameter p4 = new Parameter()
            .with(new Rule("boolean", "responseRequired", "responseRequired"))
            .with(new Rule("boolean", null, "false"));
        
        Parameter p5 = new Parameter()
            .with(new Rule("SetListener", "setListener", "setListener"))
            .with(new Rule("SetListener", null, "null"));
        
        return new MethodGenerator("doSet", "SetResult", p0, p1, p2, p3, p4, p5);
    }
    
    public MethodGenerator createDoSetGetGenerator1() {
        Parameter p0 = new Parameter()
            .with(new Rule("Node", "node", "node"))
            .with(new Rule("NodeInfo", "nodeInfo", "getRemoteNode(nodeInfo)"));
        
        Parameter p1 = new Parameter()
            .with(new Rule("EOJ", "eoj", "eoj"))
            .with(new Rule("ClassEOJ", "ceoj", "ceoj.getAllInstanceEOJ()"));
        
        Parameter p2 = new Parameter()
            .with(new Rule("EPC", "setEPC", "properties",
                    "LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();",
                    "properties.add(new Pair<EPC, Data>(setEPC, data));"));
        
        Parameter p3 = new Parameter()
            .with(new Rule("Data", "data", null));
        
        Parameter p4 = new Parameter()
            .with(new Rule("EPC", "getEPC", "toList(getEPC)"))
            .with(new Rule("List<EPC>", "epcs", "epcs"));
        
        Parameter p5 = new Parameter()
            .with(new Rule("int", "timeout", "timeout"));
        
        Parameter p6 = new Parameter()
            .with(new Rule("SetGetListener", "setGetListener", "setGetListener"))
            .with(new Rule("SetGetListener", null, "null"));
        
        return new MethodGenerator("doSetGet", "SetGetResult", p0, p1, p2, p3, p4, p5, p6);
    }
    
    public MethodGenerator createDoSetGetGenerator2() {
        Parameter p0 = new Parameter()
            .with(new Rule("Node", "node", "node"))
            .with(new Rule("NodeInfo", "nodeInfo", "getRemoteNode(nodeInfo)"));
        
        Parameter p1 = new Parameter()
            .with(new Rule("EOJ", "eoj", "eoj"))
            .with(new Rule("ClassEOJ", "ceoj", "ceoj.getAllInstanceEOJ()"));
        
        Parameter p2 = new Parameter()
            .with(new Rule("List<Pair<EPC, Data>>", "properties", "properties"));
        
        Parameter p3 = new Parameter()
            .with(new Rule("EPC", "epc", "toList(epc)"))
            .with(new Rule("List<EPC>", "epcs", "epcs"));
        
        Parameter p4 = new Parameter()
            .with(new Rule("int", "timeout", "timeout"));
        
        Parameter p5 = new Parameter()
            .with(new Rule("SetGetListener", "setGetListener", "setGetListener"))
            .with(new Rule("SetGetListener", null, "null"));
        
        return new MethodGenerator("doSetGet", "SetGetResult", p0, p1, p2, p3, p4, p5);
    }
    
    public MethodGenerator createDoObserveGenerator1() {
        Parameter p0 = new Parameter()
            .with(new Rule("Selector<? super Frame>", "selector", false, "selector"))
            .with(new Rule("Selector<? super Frame>", null, false, "new FrameSelector()"));
        
        Parameter p1 = new Parameter()
            .with(new Rule("ObserveListener", "observeListener", false, "observeListener"))
            .with(new Rule("ObserveListener", null, false, "null"));
        
        return new MethodGenerator("doObserve", "ObserveResult", p0, p1);
    }
    
    private class DoObserveFilter2 implements Filter {
        @Override
        public boolean filter(List<Rule> rules) {
            int count = 0;

            for (int i=0; i<rules.size()-1; i++) {
                if (rules.get(i).getParamName() != null) {
                    count++;
                }
            }

            return count != 0;
        }
    }
    
    public MethodGenerator createDoObserveGenerator2() {
        Parameter p0 = new Parameter()
            .with(new Rule("Node", "node", "toObjectList(node)"))
            .with(new Rule("NodeInfo", "nodeInfo", "toObjectList(nodeInfo)"))
            .with(new Rule("Node", null, false, "(List)null"));
        
        Parameter p1 = new Parameter()
            .with(new Rule("EOJ", "eoj", "toObjectList(eoj)"))
            .with(new Rule("ClassEOJ", "ceoj", "toObjectList(ceoj.getAllInstanceEOJ())"))
            .with(new Rule("EOJ", null, false, "(List)null"));
        
        Parameter p2 = new Parameter()
            .with(new Rule("List<EPC>", "epcs", "epcs"))
            .with(new Rule("EPC", "epc", "toList(epc)"))
            .with(new Rule("EPC", null, false, "(List<EPC>)null"));
        
        Parameter p3 = new Parameter()
            .with(new Rule("ObserveListener", "observeListener", false, "observeListener"))
            .with(new Rule("ObserveListener", null, false, "null"));
        
        return new MethodGenerator("doObserve", "ObserveResult", p0, p1, p2, p3).withFilter(new DoObserveFilter2());
    }
    
    private class DoObserveFilter3 implements Filter {

        @Override
        public boolean filter(List<Rule> rules) {
            Rule r1 = rules.get(1);
            Rule r2 = rules.get(2);
            
            if (r1.getType().equals("List") && r2.getParamName() == null) {
                return false;
            }
            
            if (r1.getParamName() == null && r2.getParamName() == null) {
                return false;
            }
            
            return true;
        }
    }
    
    public MethodGenerator createDoObserveGenerator3() {
        Parameter p0 = new Parameter()
            .with(new Rule("List", "nodes", "nodes"));
        
        Parameter p1 = new Parameter()
            .with(new Rule("List", "eojs", "eojs"))
            .with(new Rule("EOJ", "eoj", "toObjectList(eoj)"))
            .with(new Rule("ClassEOJ", "ceoj", "toObjectList(ceoj.getAllInstanceEOJ())"))
            .with(new Rule("List", null, "(List)null"));
        
        Parameter p2 = new Parameter()
            .with(new Rule("List<EPC>", "epcs", "epcs"))
            .with(new Rule("EPC", "epc", "toList(epc)"))
            .with(new Rule("List", null, "(List<EPC>)null"));
        
        Parameter p3 = new Parameter()
            .with(new Rule("ObserveListener", "observeListener", "observeListener"))
            .with(new Rule("ObserveListener", null, "null"));
        
        return new MethodGenerator("doObserve", "ObserveResult", p0, p1, p2, p3).withFilter(new DoObserveFilter3());
    }
    
    private class DoNotifyFilter1 implements Filter {

        @Override
        public boolean filter(List<Rule> rules) {
            Rule r4 = rules.get(4);
            Rule r5 = rules.get(5);
            
            if (r4.getParamName() == null && r5.getParamName() == null) {
                return r5.getConverted().equals("false");
            }
            
            if (r4.getParamName() == null && r5.getParamName() != null) {
                return false;
            }
            
            if (r4.getParamName() != null && r5.getParamName() == null) {
                return r5.getConverted().equals("true");
            }
            
            return true;
        }
    }
    
    public MethodGenerator createDoNotifyGenerator1() {
        Parameter p0 = new Parameter()
            .with(new Rule("Node", "node", "node"))
            .with(new Rule("NodeInfo", "nodeInfo", "getRemoteNode(nodeInfo)"))
            .with(new Rule("Node", null, "(Node)null"));
        
        Parameter p1 = new Parameter()
            .with(new Rule("EOJ", "eoj", "eoj"));
        
        Parameter p2 = new Parameter()
            .with(new Rule("EPC", "epc", "properties",
                    "LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();",
                    "properties.add(new Pair<EPC, Data>(epc, data));"));
        
        Parameter p3 = new Parameter()
            .with(new Rule("Data", "data", null));
        
        Parameter p4 = new Parameter()
            .with(new Rule("int", "timeout", "timeout"))
            .with(new Rule("int", null, "0"));
        
        Parameter p5 = new Parameter()
            .with(new Rule("boolean", "responseRequired", "responseRequired"))
            .with(new Rule("boolean", null, "true"))
            .with(new Rule("boolean", null, "false"));
        
        Parameter p6 = new Parameter()
            .with(new Rule("NotifyListener", "notifyListener", "notifyListener"))
            .with(new Rule("NotifyListener", null, "null"));
        
        return new MethodGenerator("doNotify", "NotifyResult", p0, p1, p2, p3, p4, p5, p6).withFilter(new DoNotifyFilter1());
    }
    
    private class DoNotifyFilter2 implements Filter {

        @Override
        public boolean filter(List<Rule> rules) {
            Rule r3 = rules.get(3);
            Rule r4 = rules.get(4);
            
            if (r3.getParamName() == null && r4.getParamName() == null) {
                return r4.getConverted().equals("false");
            }
            
            if (r3.getParamName() == null && r4.getParamName() != null) {
                return false;
            }
            
            if (r3.getParamName() != null && r4.getParamName() == null) {
                return r4.getConverted().equals("true");
            }
            
            return true;
        }
    }
    
    public MethodGenerator createDoNotifyGenerator2() {
        Parameter p0 = new Parameter()
            .with(new Rule("Node", "node", "node"))
            .with(new Rule("NodeInfo", "nodeInfo", "getRemoteNode(nodeInfo)"))
            .with(new Rule("Node", null, "(Node)null"));
        
        Parameter p1 = new Parameter()
            .with(new Rule("EOJ", "eoj", "eoj"));
        
        Parameter p2 = new Parameter()
            .with(new Rule("List<Pair<EPC, Data>>", "properties", "properties"));
        
        Parameter p3 = new Parameter()
            .with(new Rule("int", "timeout", "timeout"))
            .with(new Rule("int", null, "0"));
        
        Parameter p4 = new Parameter()
            .with(new Rule("boolean", "responseRequired", "responseRequired"))
            .with(new Rule("boolean", null, "true"))
            .with(new Rule("boolean", null, "false"));
        
        Parameter p5 = new Parameter()
            .with(new Rule("NotifyListener", "notifyListener", "notifyListener"))
            .with(new Rule("NotifyListener", null, "null"));
        
        return new MethodGenerator("doNotify", "NotifyResult", p0, p1, p2, p3, p4, p5).withFilter(new DoNotifyFilter2());
    }
    
    private class DoNotifyInstanceListFilter implements Filter {

        @Override
        public boolean filter(List<Rule> rules) {
            Rule r1 = rules.get(1);
            Rule r2 = rules.get(2);
            
            if (r1.getParamName() == null && r2.getParamName() == null) {
                return r2.getConverted().equals("false");
            }
            
            if (r1.getParamName() == null && r2.getParamName() != null) {
                return false;
            }
            
            if (r1.getParamName() != null && r2.getParamName() == null) {
                return r2.getConverted().equals("true");
            }
            
            return true;
        }
    }
    
    public MethodGenerator createDoNotifyInstanceListGenerator() {
        Parameter p0 = new Parameter()
            .with(new Rule("Node", "node", "node"))
            .with(new Rule("NodeInfo", "nodeInfo", "getRemoteNode(nodeInfo)"))
            .with(new Rule("Node", null, "(Node)null"));
        
        Parameter p1 = new Parameter()
            .with(new Rule("int", "timeout", "timeout"))
            .with(new Rule("int", null, "0"));
        
        Parameter p2 = new Parameter()
            .with(new Rule("boolean", "responseRequired", "responseRequired"))
            .with(new Rule("boolean", null, "true"))
            .with(new Rule("boolean", null, "false"));
        
        Parameter p3 = new Parameter()
            .with(new Rule("NotifyListener", "notifyListener", "notifyListener"))
            .with(new Rule("NotifyListener", null, "null"));
        
        return new MethodGenerator("doNotifyInstanceList", "NotifyResult", p0, p1, p2, p3).withFilter(new DoNotifyInstanceListFilter());
    }
    
    private List<MethodGenerator> createGenerators() {
        
        LinkedList<MethodGenerator> generators = new LinkedList<MethodGenerator>();
        
        generators.add(createDoGetGenerator());
        generators.add(createDoSetGenerator1());
        generators.add(createDoSetGenerator2());
        generators.add(createDoSetGetGenerator1());
        generators.add(createDoSetGetGenerator2());
        generators.add(createDoObserveGenerator1());
        generators.add(createDoObserveGenerator2());
        generators.add(createDoObserveGenerator3());
        generators.add(createDoNotifyGenerator1());
        generators.add(createDoNotifyGenerator2());
        generators.add(createDoNotifyInstanceListGenerator());
        
        return generators;
    }
    
    public List<String> generateInterfaces() {
        
        List<String> ret = new LinkedList<String>();
        
        for (MethodGenerator generator : createGenerators()) {
            ret.addAll(generator.generateInterfaces());
        }
        
        return ret;
    }
    
    public List<String> generateMethods() {
        
        List<String> ret = new LinkedList<String>();
        
        for (MethodGenerator generator : createGenerators()) {
            ret.addAll(generator.generateMethods());
        }
        
        return ret;
    }
    
    private static void showUsage() {
        System.out.println("Usage: ServiceDoMethodsGenerator [interfaces]");
    }
        
    public static void main(String[] args) {
        boolean shouldGenerateInterface = false;
        
        if (args.length == 1) {
            if (args[0].equals("interfaces")) {
                shouldGenerateInterface = true;
            } else {
                showUsage();
                return;
            }
        }
        
        if (1 < args.length) {
            showUsage();
            return;
        }
        
        ServiceDoMethodsGenerator generator = new ServiceDoMethodsGenerator();
        
        if (shouldGenerateInterface) {
            for (String str : generator.generateInterfaces()) {
                System.out.print(str);
            }
        } else {
            for (String str : generator.generateMethods()) {
                System.out.println(str);
            }
        }
    }
}
