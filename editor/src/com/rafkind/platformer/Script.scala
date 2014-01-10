package com.rafkind.platformer

import java.awt._
import java.awt.image._
import javax.swing._
import java.io._
import javax.imageio._
import java.awt.event._
import javax.swing.event._
import org.swixml.SwingEngine
import javax.swing.filechooser.FileFilter

import scala.collection.immutable.List

import com.rafkind.paintown.exception.LoadException
import com.rafkind.paintown.TokenReader
import com.rafkind.paintown.Token
import com.rafkind.paintown.MaskedImage

class ScriptObject(var name:String){
    var x:Int = 0
    var y:Int = 0
    var image:ImageHolder = null
    var width:Int = 16
    var height:Int = 16
    var module:String = ""
    var function:String = ""
    var color:Color = new Color(0,0,128)
    var font = new Font("Verdana", Font.BOLD, 4)
    
    def render(g:Graphics2D, x1:Int, y1:Int) = {
        g.setColor(color)
        if (image != null){
            image.render(g,x1 + x, y1 + y)
        } else {
            g.fillRect(x1 + x, y1 + y, width, height)
        }
        g.setFont(font);
        g.drawString( "Object [" + name + "]", x1 + x -1, y1 + y -1)
    }
    
    def readToken(token:Token) = {
        if (!token.getName().equals("object-script")){
            throw new LoadException( "Starting token is not a 'script object'" )
        }
        val nameToken = token.findToken("id")
        if (nameToken != null){
            name = nameToken.readString(0)
        }
        
        val positionToken = token.findToken("position")
        if (positionToken != null){
            x = positionToken.readInt(0)
            y = positionToken.readInt(1)
            if (positionToken.hasIndex(2) && positionToken.hasIndex(3)){
                width = positionToken.readInt(2)
                height = positionToken.readInt(3)
            }
        }
        
        val moduleToken = token.findToken("module")
        if (moduleToken != null){
            module = moduleToken.readString(0)
        }
        
        val functionToken = token.findToken("function")
        if (functionToken != null){
            function = functionToken.readString(0)
        }
        
        val imageToken = token.findToken("image")
        if (imageToken != null){
            val imageName = imageToken.readString(0)
            image = new ImageHolder(new File("."), new File(imageName))
            width = image.getWidth()
            height = image.getHeight()
        }
        
        val colorToken = token.findToken("color")
        if (colorToken != null){
            val r = colorToken.readInt(0)
            val g = colorToken.readInt(1)
            val b = colorToken.readInt(2)
            color = new Color(r,g,b)
        }
    }
    
    def toToken():Token = {
        val script = new Token()
        script.addToken(new Token(script, "object-script"))
        script.addToken(Array("id", name))
        script.addToken(Array("position", x.toString, y.toString, width.toString, height.toString))
        script.addToken(Array("module", module))
        script.addToken(Array("function", function))
        if (image != null){
            script.addToken(Array("image", image.toString()))
        }
        script.addToken(Array("color", color.getRed().toString, color.getGreen().toString, color.getBlue().toString))
        
        script
    }
    
    override def toString():String = {
        toToken().toString()
    }
    
    def editDialog(view:JPanel, viewScroll:JScrollPane, list:JList[ScriptObject]) = {
        try {
            val engine = new SwingEngine("platformer/script-object.xml")
            val pane = engine.find("dialog").asInstanceOf[JDialog]
            
            {
                val nameField = engine.find("name").asInstanceOf[JTextField]
                nameField.setText(name)
                nameField.getDocument().addDocumentListener(new DocumentListener() {
                    def changedUpdate(e:DocumentEvent) = {
                        update()
                    }
                    def removeUpdate(e:DocumentEvent) = {
                        update()
                    }
                    def insertUpdate(e:DocumentEvent) = {
                        update()
                    }
                    
                    def update() = {
                        name = nameField.getText()
                        list.revalidate()
                        list.repaint()
                        view.revalidate()
                        viewScroll.repaint()
                    }
                })
            }
            
            // script
            {
                val moduleField = engine.find("module").asInstanceOf[JTextField]
                moduleField.setText(module)
                moduleField.getDocument().addDocumentListener(new DocumentListener() {
                    def changedUpdate(e:DocumentEvent) = {
                        update()
                    }
                    def removeUpdate(e:DocumentEvent) = {
                        update()
                    }
                    def insertUpdate(e:DocumentEvent) = {
                        update()
                    }
                    
                    def update() = {
                        module = moduleField.getText()
                        list.revalidate()
                        list.repaint()
                    }
                })
                
                val functionField = engine.find("function").asInstanceOf[JTextField]
                functionField.setText(function)
                functionField.getDocument().addDocumentListener(new DocumentListener() {
                    def changedUpdate(e:DocumentEvent) = {
                        update()
                    }
                    def removeUpdate(e:DocumentEvent) = {
                        update()
                    }
                    def insertUpdate(e:DocumentEvent) = {
                        update()
                    }
                    
                    def update() = {
                        function = functionField.getText()
                        list.revalidate()
                        list.repaint()
                    }
                })
            }
            
            // x
            {
                val xspin = engine.find("x").asInstanceOf[JSpinner]
                val model = new SpinnerNumberModel()
                xspin.setModel(model)
                model.setValue(x)
                xspin.addChangeListener(new ChangeListener(){
                    override def stateChanged(event:ChangeEvent){
                        val spinner = event.getSource().asInstanceOf[JSpinner]
                        val i = spinner.getValue().asInstanceOf[java.lang.Integer]
                        x = i.intValue()
                        view.revalidate()
                        viewScroll.repaint()
                    }
                })
            }
            
            // y
            {
                val yspin = engine.find("y").asInstanceOf[JSpinner]
                val model = new SpinnerNumberModel()
                yspin.setModel(model)
                model.setValue(y)
                yspin.addChangeListener(new ChangeListener(){
                    override def stateChanged(event:ChangeEvent){
                        val spinner = event.getSource().asInstanceOf[JSpinner]
                        val i = spinner.getValue().asInstanceOf[java.lang.Integer]
                        y = i.intValue()
                        view.revalidate()
                        viewScroll.repaint()
                    }
                })
            }
            
            // width
            {
                val widthspin = engine.find("width").asInstanceOf[JSpinner]
                val model = new SpinnerNumberModel()
                widthspin.setModel(model)
                model.setValue(width)
                widthspin.addChangeListener(new ChangeListener(){
                    override def stateChanged(event:ChangeEvent){
                        val spinner = event.getSource().asInstanceOf[JSpinner]
                        val i = spinner.getValue().asInstanceOf[java.lang.Integer]
                        width = i.intValue()
                        view.revalidate()
                        viewScroll.repaint()
                    }
                })
            }
            
            // height
            {
                val heightspin = engine.find("height").asInstanceOf[JSpinner]
                val model = new SpinnerNumberModel()
                heightspin.setModel(model)
                model.setValue(height)
                heightspin.addChangeListener(new ChangeListener(){
                    override def stateChanged(event:ChangeEvent){
                        val spinner = event.getSource().asInstanceOf[JSpinner]
                        val i = spinner.getValue().asInstanceOf[java.lang.Integer]
                        height = i.intValue()
                        view.revalidate()
                        viewScroll.repaint()
                    }
                })
            }
            
            // Color
            {
                val viewColor = new JPanel(){
                    override def getPreferredSize():Dimension = {
                        new Dimension(25,25)
                    }

                    override def paintComponent(g:Graphics){
                        g.setColor(color)
                        g.fillRect(0, 0, this.getWidth(), this.getHeight())
                        g.setColor(Color.BLACK)
                        g.drawRect(0,0,this.getWidth()-1,this.getHeight()-1)
                    }
                }
                val colorPanel = engine.find("color-display").asInstanceOf[JPanel]
                colorPanel.add(viewColor)
                
                val button = engine.find("color").asInstanceOf[JButton]
                button.addActionListener(new ActionListener() {
                    def actionPerformed(e:ActionEvent) = {
                        val chosen = JColorChooser.showDialog(button, "Select a Fill Color", color);
                        if (chosen != null){
                            color = chosen
                            view.revalidate()
                            viewScroll.repaint()
                            viewColor.revalidate()
                            colorPanel.repaint()
                        }
                    }
                })
            }
            
            // Image
            {
                var scriptField = engine.find("image-location").asInstanceOf[JTextField]
                scriptField.setEditable(false)
                if (image != null){
                    scriptField.setText(image.toString())
                }
                val set = engine.find("set-image-button").asInstanceOf[JButton]
                set.addActionListener(new ActionListener() { 
                    def actionPerformed(e:ActionEvent) = {
                        val chooser = new JFileChooser(MapEditor.getDataPath("/"))
                        chooser.setFileFilter(new FileFilter(){
                            def accept(f:File):Boolean = {
                                f.isDirectory() || f.getName().endsWith( ".png" )
                            }

                            def getDescription():String = {
                                "Png files"
                            }
                        })
                        val returnVal = chooser.showOpenDialog(pane)
                        if (returnVal == JFileChooser.APPROVE_OPTION){
                            val choosen:File = chooser.getSelectedFile()
                            image = new ImageHolder(new File("."), MapEditor.absoluteToRelative(choosen))
                            scriptField.setText(image.toString())
                            view.revalidate()
                            viewScroll.repaint()
                            list.revalidate()
                            list.repaint()
                        }
                    } 
                })
            }
            
            // Close
            {
                val close = engine.find("close").asInstanceOf[JButton]
                close.addActionListener(new ActionListener() { 
                    def actionPerformed(e:ActionEvent) = {
                        pane.setVisible(false)
                        view.revalidate()
                        viewScroll.repaint()
                        list.revalidate()
                        list.repaint()
                    } 
                })
            }
            
            // Show Dialog
            pane.repaint()
            pane.setModal(true)
            pane.setVisible(true)
        } catch {
            case e:Exception => JOptionPane.showMessageDialog(null, "error on opening, reason: " + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE)
        }
    }
}

class ScriptObjectData extends ListModel[ScriptObject] {
    var data:List[ScriptObject] = List[ScriptObject]()
    var listeners = List[ListDataListener]()

    def add(obj:ScriptObject){
        data = data :+ obj
        val event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, data.size, data.size)
        for (listener <- listeners){
            listener.intervalAdded(event)
        }
    }
    
    def remove(index:Int){
        data = data.remove(data.indexOf(_) == index)
        val event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index)
        for (listener <- listeners){
            listener.intervalAdded(event)
        }
    }

    def getAll():List[ScriptObject] = {
        data
    }

    override def addListDataListener(listener:ListDataListener){
        listeners = listeners :+ listener
    }

    override def getElementAt(index:Int) = {
        this.data.find(data.indexOf(_) == index) match {
            case Some(obj) => obj
            case None => throw new Exception("failed to find " + index)
        }
    }

    override def getSize():Int = {
        this.data.size
    }

    override def removeListDataListener(listener:ListDataListener){
        listeners = this.listeners - listener
    }
    
    def render(g:Graphics2D, x:Int, y:Int) = {
        data.foreach {
            case (script) => script.render(g,x,y)
        }
    }
    
    def readToken(token:Token) = {
        val scriptIterator = token.findTokens("object-script").iterator()
        while(scriptIterator.hasNext()){
            val script = new ScriptObject("Script")
            script.readToken(scriptIterator.next())
            add(script)
        }
    }
}
