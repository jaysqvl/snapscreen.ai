import { NavbarWrapper } from "@/components/ui/navbar-wrapper";

export default function MainLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <>
      <NavbarWrapper />
      <main className="pt-14">
        {children}
      </main>
    </>
  );
} 