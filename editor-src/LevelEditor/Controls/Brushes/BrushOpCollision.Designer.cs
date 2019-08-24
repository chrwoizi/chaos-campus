namespace Javy.Controls.Brushes
{
    partial class BrushOpCollision
    {
        /// <summary> 
        /// Erforderliche Designervariable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary> 
        /// Verwendete Ressourcen bereinigen.
        /// </summary>
        /// <param name="disposing">True, wenn verwaltete Ressourcen gelöscht werden sollen; andernfalls False.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Vom Komponenten-Designer generierter Code

        /// <summary> 
        /// Erforderliche Methode für die Designerunterstützung. 
        /// Der Inhalt der Methode darf nicht mit dem Code-Editor geändert werden.
        /// </summary>
        private void InitializeComponent()
        {
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.collisionAdd = new System.Windows.Forms.RadioButton();
            this.collisionSet = new System.Windows.Forms.RadioButton();
            this.side = new System.Windows.Forms.ComboBox();
            this.label2 = new System.Windows.Forms.Label();
            this.collisionButton1 = new Javy.Controls.CollisionButton();
            this.groupBox1.SuspendLayout();
            this.SuspendLayout();
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.side);
            this.groupBox1.Controls.Add(this.label2);
            this.groupBox1.Controls.Add(this.collisionAdd);
            this.groupBox1.Controls.Add(this.collisionSet);
            this.groupBox1.Controls.Add(this.collisionButton1);
            this.groupBox1.Location = new System.Drawing.Point(3, 3);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(150, 210);
            this.groupBox1.TabIndex = 1;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Collisionbrush Options";
            // 
            // collisionAdd
            // 
            this.collisionAdd.AutoSize = true;
            this.collisionAdd.Location = new System.Drawing.Point(6, 187);
            this.collisionAdd.Name = "collisionAdd";
            this.collisionAdd.Size = new System.Drawing.Size(84, 17);
            this.collisionAdd.TabIndex = 2;
            this.collisionAdd.Text = "Add collision";
            this.collisionAdd.UseVisualStyleBackColor = true;
            // 
            // collisionSet
            // 
            this.collisionSet.AutoSize = true;
            this.collisionSet.Checked = true;
            this.collisionSet.Location = new System.Drawing.Point(6, 163);
            this.collisionSet.Name = "collisionSet";
            this.collisionSet.Size = new System.Drawing.Size(81, 17);
            this.collisionSet.TabIndex = 1;
            this.collisionSet.TabStop = true;
            this.collisionSet.Text = "Set collision";
            this.collisionSet.UseVisualStyleBackColor = true;
            // 
            // side
            // 
            this.side.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.side.FormattingEnabled = true;
            this.side.Items.AddRange(new object[] {
            "Both",
            "Inner",
            "Outer"});
            this.side.Location = new System.Drawing.Point(9, 133);
            this.side.Name = "side";
            this.side.Size = new System.Drawing.Size(121, 21);
            this.side.TabIndex = 7;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(6, 117);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(28, 13);
            this.label2.TabIndex = 6;
            this.label2.Text = "Side";
            // 
            // collisionButton1
            // 
            this.collisionButton1.CollisionBottom = false;
            this.collisionButton1.CollisionLeft = false;
            this.collisionButton1.CollisionRight = false;
            this.collisionButton1.CollisionTop = false;
            this.collisionButton1.Cursor = System.Windows.Forms.Cursors.Hand;
            this.collisionButton1.Location = new System.Drawing.Point(21, 23);
            this.collisionButton1.Name = "collisionButton1";
            this.collisionButton1.Size = new System.Drawing.Size(84, 81);
            this.collisionButton1.TabIndex = 0;
            // 
            // BrushOpCollision
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.AutoSize = true;
            this.Controls.Add(this.groupBox1);
            this.Name = "BrushOpCollision";
            this.Size = new System.Drawing.Size(476, 440);
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.RadioButton collisionAdd;
        private System.Windows.Forms.RadioButton collisionSet;
        private CollisionButton collisionButton1;
        private System.Windows.Forms.ComboBox side;
        private System.Windows.Forms.Label label2;

    }
}
