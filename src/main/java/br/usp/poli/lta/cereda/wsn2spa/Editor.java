/**
* ------------------------------------------------------
*    Laboratório de Linguagens e Técnicas Adaptativas
*       Escola Politécnica, Universidade São Paulo
* ------------------------------------------------------
* 
* This program is free software: you can redistribute it
* and/or modify  it under the  terms of the  GNU General
* Public  License  as  published by  the  Free  Software
* Foundation, either  version 3  of the License,  or (at
* your option) any later version.
* 
* This program is  distributed in the hope  that it will
* be useful, but WITHOUT  ANY WARRANTY; without even the
* implied warranty  of MERCHANTABILITY or FITNESS  FOR A
* PARTICULAR PURPOSE. See the GNU General Public License
* for more details.
* 
**/
package br.usp.poli.lta.cereda.wsn2spa;

import br.usp.poli.lta.cereda.nfa2dfa.utils.Conversion;
import br.usp.poli.lta.cereda.nfa2dfa.utils.Reader;
import br.usp.poli.lta.cereda.nfa2dfa.utils.SimpleTransition;
import br.usp.poli.lta.cereda.nfa2dfa.utils.Triple;
import br.usp.poli.lta.cereda.mwirth2ape.exporter.Spec;
import br.usp.poli.lta.cereda.mwirth2ape.exporter.Writer;
import br.usp.poli.lta.cereda.mwirth2ape.mwirth.Generator;
import br.usp.poli.lta.cereda.mwirth2ape.mwirth.MWirthLexer;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class Editor extends JFrame {

    private final JTextField txtFile;
    private final JTextField txtDotOutput;
    private final JTextField txtYamlOutput;
    private final JCheckBox checkDFAConvert;
    private final JCheckBox checkMinimize;
    private final JButton btnOpen;
    private final JButton btnRun;
    private final JFileChooser chooser;

    private File file;
    
    public Editor() {
        super("MWSN2SPA");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new MigLayout());
        
        txtDotOutput = new JTextField(15);
        txtYamlOutput = new JTextField(15);
        txtFile = new JTextField(10);
        txtFile.setEditable(false);
        
        checkDFAConvert = new JCheckBox("Convert submachines to DFA's");
        checkMinimize = new JCheckBox("Apply state minimization");
        checkMinimize.setEnabled(false);
        btnOpen = new JButton(new ImageIcon(getClass().
                getResource("/br/usp/poli/lta/cereda/mwsn2spa/images/open.png")));
        btnRun = new JButton("Convert MWSN to SPA", new ImageIcon(getClass().
                getResource("/br/usp/poli/lta/cereda/mwsn2spa/images/play.png")));
        
        chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        FileNameExtensionFilter filter =
                new FileNameExtensionFilter("Text files", "txt", "text");
        chooser.setFileFilter(filter);
        
        btnOpen.addActionListener((ActionEvent ae) -> {
            int value = chooser.showOpenDialog(Editor.this);
            if (value == JFileChooser.APPROVE_OPTION) {
                file = chooser.getSelectedFile();
                txtFile.setText(file.getName());
            }
        });
        
        checkDFAConvert.addChangeListener((ChangeEvent ce) -> {
            checkMinimize.setEnabled(checkDFAConvert.isSelected());
            if (!checkDFAConvert.isSelected()) {
                checkMinimize.setSelected(false);
            }
        });
        
        btnRun.addActionListener((ActionEvent ae) -> {
            boolean restore = checkMinimize.isEnabled();
            state(false, txtDotOutput, txtYamlOutput, btnOpen, btnRun,
                    checkDFAConvert, checkMinimize);
            try {
                if (!filled(txtDotOutput, txtYamlOutput, txtFile)) {
                    throw new Exception("The fields could not be empty. Make "
                            + "sure to select the grammar file and provide "
                            + "both DOT and YAML patterns in their respective "
                            + "fields.");
                }
                if (!valid(txtDotOutput, txtYamlOutput)) {
                    throw new Exception("The DOT and YAML fields lack the "
                            + "replacement pattern '%s' in order to generate "
                            + "files corresponding to each submachine in the "
                            + "automaton model. Make sure to include the "
                            + "pattern.");
                }
                if (!file.exists()) {
                    throw new Exception("The provided grammar file '" + "' does"
                        + " not exist. Make sure the location is correct and"
                        + " try again.");
                }
                
                String text = FileUtils.readFileToString(file, "UTF-8").trim();
                MWirthLexer wl = new MWirthLexer(text);
                Generator g = new Generator(wl, 1);
                g.generateAutomaton();

                Writer writer = new Writer(g.getTransitions());
                Map<String, String> map =
                        writer.generateYAMLMap(txtYamlOutput.getText().trim());
            
                if (Utils.neither(checkDFAConvert, checkMinimize)) {
                    br.usp.poli.lta.cereda.mwirth2ape.dot.Dot dot =
                            new br.usp.poli.lta.cereda.mwirth2ape.dot.Dot(
                                    g.getTransitions()
                            );
                    dot.generate(txtDotOutput.getText().trim());
                    for (String key : map.keySet()) {
                        FileUtils.write(new File(key), map.get(key), "UTF-8");
                    }
                } else {
                    for (String key : map.keySet()) {
                        Triple<Integer, Set<Integer>, List<SimpleTransition>> spec =
                                Reader.read(map.get(key));
                        br.usp.poli.lta.cereda.nfa2dfa.dot.Dot dot =
                                new br.usp.poli.lta.cereda.nfa2dfa.dot.Dot();
                        dot.append(Reader.getName(), "original", spec);

                        Conversion c;

                        if (checkDFAConvert.isSelected()) {
                            c = new Conversion(spec.getThird(), spec.getFirst(),
                                    spec.getSecond());
                            spec = c.convert();
                            dot.append(Reader.getName().concat("'"),
                                    "converted", spec);
                        }

                        if (checkMinimize.isSelected()) {
                            c = new Conversion(spec.getThird(), spec.getFirst(),
                                    spec.getSecond());
                            spec = c.minimize();
                            dot.append(Reader.getName().concat("''"), "minimized",
                                    spec);
                        }

                        Yaml yaml = new Yaml();
                        Spec result = Utils.toFormat(spec);
                        result.setName(Reader.getName());
                        map.put(key, yaml.dump(result));

                        String dotname = String.format(txtDotOutput.getText().trim(),
                                Reader.getName());
                        dot.dump(dotname);

                    }

                    for (String key : map.keySet()) {
                        FileUtils.write(new File(key), map.get(key), "UTF-8");
                    }
                }
                
                showMessage("Success!", "The structured pushdown automaton "
                        + "spec was successfully generated from the provided "
                        + "grammar file.");
                
            }
            catch (Exception exception) {
                showException("An exception was thrown", exception);
            }
            state(true, txtDotOutput, txtYamlOutput, btnOpen, btnRun,
                    checkDFAConvert, checkMinimize);
            checkMinimize.setEnabled(restore);
        });
        
        add(new JLabel("Grammar file:"));
        add(txtFile);
        add(btnOpen, "growx, wrap");
        add(new JLabel("DOT pattern:"));
        add(txtDotOutput, "growx, span 2, wrap");
        add(new JLabel("YAML pattern:"));
        add(txtYamlOutput, "growx, span 2, wrap");
        add(checkDFAConvert, "span 3, wrap");
        add(checkMinimize, "span 3, wrap");
        add(btnRun, "growx, span 3");
        
        pack();
        setLocationRelativeTo(null);
        
    }
        
    private boolean filled(JTextField... fields) {
        for (JTextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    private boolean valid(JTextField... fields) {
        for (JTextField field : fields) {
            if (!field.getText().contains("%s")) {
                return false;
            }
        }
        return true;
    }
    
    private void showException(String title, Exception exception) {
        String html = String.format("<html><body style=\"width:250px\">%s</body></html>", exception.getMessage());
        JOptionPane.showMessageDialog(this, html, title, JOptionPane.ERROR_MESSAGE);
    }
    
    private void showMessage(String title, String message) {
        String html = String.format("<html><body style=\"width:250px\">%s</body></html>", message);
        JOptionPane.showMessageDialog(this, html, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void state(boolean status, JComponent... components) {
        for (JComponent component : components) {
            component.setEnabled(status);
        }
    }
    
}
